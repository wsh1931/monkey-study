package com.monkey.monkeyblog.service.Impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.monkey.monkeyUtils.constants.CommunityMenuEnum;
import com.monkey.monkeyUtils.constants.ExceptionEnum;
import com.monkey.monkeyUtils.email.EmailContentConstant;
import com.monkey.monkeyUtils.email.EmailTitleConstant;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.CommunityManageMapper;
import com.monkey.monkeyUtils.pojo.CommunityManage;
import com.monkey.monkeyUtils.redis.RedisKeyAndTimeEnum;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.pojo.Vo.EmailCodeVo;
import com.monkey.monkeyblog.pojo.Vo.RegisterVo;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.service.UserService;

import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.springsecurity.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private LineCaptcha lineCaptcha;
    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String sender;

    @Resource
    private JavaMailSender mailSender;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private CommunityManageMapper communityManageMapper;

    // 用户注册
    @Override
    public ResultVO userRegister(RegisterVo registerVo) {
        String username = registerVo.getUsername();
        String password = registerVo.getPassword();
        String confirmPassword = registerVo.getConfirmPassword();
        String email = registerVo.getEmail();
        String verifyCode = registerVo.getVerifyCode();
        verifyCode = verifyCode.toLowerCase();
        if (!this.isQQEmail(email)) {
            return new ResultVO(ResultStatus.NO, "请输入正确的邮箱", null);
        }
        String redisKey = RedisKeyAndTimeEnum.VERFY_CODE_REGISTER.getKeyName() + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            RegisterVo registerVoCode = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(redisKey), RegisterVo.class) ;
            String verifyCodeBefore = registerVoCode.getVerifyCode();
            verifyCodeBefore = verifyCodeBefore.toLowerCase();
            String emailBefore = registerVoCode.getEmail();
            if (!emailBefore.equals(email)) {
                return new ResultVO(ResultStatus.NO, "邮箱发生变动，请重试", null);
            }
            if (!verifyCodeBefore.equals(verifyCode)) {
                return new ResultVO(ResultStatus.NO, "验证码错误，请重试", null);
            }
            Boolean delete = stringRedisTemplate.delete(redisKey);
            if (!delete) {
                log.error("验证码删除异常");
            }
        } else {
            return new ResultVO(ResultStatus.NO, "验证码错误/已过期/邮箱存在修改，请重新输入", null);
        }
        username = username.trim(); // 删除首位空白字符
        if (username.length() == 0) {
            return new ResultVO(ResultStatus.NO, "用户名不能为空", null);
        }

        if (password == null || password.length() == 0) {
            return new ResultVO(ResultStatus.NO, "密码不能为空", null);
        }

        if (username.length() > 30) {
            return new ResultVO(ResultStatus.NO, "用户名长度不能大于30", null);
        }

        if (password.length() > 20) {
            return new ResultVO(ResultStatus.NO, "密码长度不能大于30", null);
        }

        if (!password.equals(confirmPassword)) {
            return new ResultVO(ResultStatus.NO, "两次密码不一致", null);
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        Long selectCount = userMapper.selectCount(userQueryWrapper);
        if (selectCount > 0) {
            return new ResultVO(ResultStatus.NO, "该用户名已存在，请重新输入", null);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User selectOne = userMapper.selectOne(queryWrapper);
        if (selectOne != null) {
            return new ResultVO(ResultStatus.NO, "该邮箱已被注册过，请重新输入", null);
        }

        String encode = passwordEncoder.encode(password);
        User user = new User();
        user.setPassword(encode);
        user.setUsername(username);
        user.setEmail(email);
        user.setRegisterTime(new Date());
        int insert = userMapper.insert(user);
        if (insert > 0) {
            return new ResultVO(ResultStatus.OK, "注册成功", null);
        }

        return new ResultVO(ResultStatus.OK, "注册失败", null);
    }

    // 通过token得到用户信息
    @Override
    public ResultVO getUserInfoByToken() {
        // 从token中获取用户名与 密码
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) usernamePasswordAuthenticationToken.getPrincipal();
        User user = userDetails.getUser();
        return new ResultVO(ResultStatus.OK, null, user);
    }

    // 判断一个邮箱是否是QQ邮箱
    public boolean isQQEmail(String email) {
        String qqRegex = "\\d+@qq\\.com"; // QQ邮箱的正则表达式
        return email.matches(qqRegex);
    }

    /**
     * 发送验证码去对方邮箱
     *
     * @param targetEmail 目标邮箱
     * @param isRegister
     * @return {@link null}
     * @author Anna.
     * @date 2023/7/6 9:00
     */
    @Override
    public ResultVO sendVerfyCode(String targetEmail, String isRegister) {
        if (!this.isQQEmail(targetEmail)) {
            return new ResultVO(ResultStatus.NO, "邮箱输入错误，请确定输入的是QQ邮箱.", null);
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", targetEmail);
        User user = userMapper.selectOne(userQueryWrapper);
        if ("true".equals(isRegister)) {
            if (user == null) {
                return new ResultVO(ResultStatus.NO, "该邮箱未被注册，请先注册", null);
            }
        } else if ("false".equals(isRegister)){
            if (user != null) {
                return new ResultVO(ResultStatus.NO, "该邮箱已被注册过，请重新输入", null);
            }
        }

        String verifyCode = getVerifyCode(4);
        SimpleMailMessage message = new SimpleMailMessage();
        // 发送者
        message.setFrom(this.sender);
        // 接收者，可设置多个接收者
        message.setTo(targetEmail);
        // 文章标题
        message.setSubject(EmailTitleConstant.REGISTER_VERFY_CODE_TITLE);
        String content = EmailContentConstant.REGISTER_VERFY_EMAIL_CONTANT + verifyCode;
        message.setText(content);
        try {
            String redisKey = RedisKeyAndTimeEnum.VERFY_CODE_REGISTER.getKeyName();
            Integer timeUnit = RedisKeyAndTimeEnum.VERFY_CODE_REGISTER.getTimeUnit();
            mailSender.send(message);
            stringRedisTemplate.opsForValue().set(redisKey + targetEmail, verifyCode);
            stringRedisTemplate.expire(redisKey + targetEmail, 5, TimeUnit.MINUTES);
            // 将信息放入消息队列中插入数据库
            String uuid = UUID.randomUUID().toString();
            EmailCodeVo emailCodeVo = new EmailCodeVo();
            emailCodeVo.setSenderEmail(this.sender);
            emailCodeVo.setEmailTitle(EmailTitleConstant.REGISTER_VERFY_CODE_TITLE);
            emailCodeVo.setEmailContent(content);
            emailCodeVo.setCreateTime(new Date());
            emailCodeVo.setReceiverEmail(targetEmail);
            emailCodeVo.setTryCount(0);
            String str = JSONObject.toJSONString(emailCodeVo);

            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setReceivedExchange(RabbitmqExchangeName.EMAIL_CODE_EXCHANGE);
            messageProperties.setReceivedRoutingKey(RabbitmqRoutingName.EMAIL_CODE);
            messageProperties.setReceivedUserId(uuid);

            Message msg = new Message(str.getBytes(), messageProperties);
//            Message rabbitmqMessage = new Message(str.getBytes(), messageProperties);
//            CorrelationData correlationData = new CorrelationData();
//            ReturnedMessage returnedMessage = new ReturnedMessage(msg,
//                    200,
//                    "邮箱验证码",
//                    RabbitmqExchangeName.EMAIL_CODE_EXCHANGE,
//                    CommonRabbitmqRoutingName.EMAIL_CODE);
//
//            correlationData.setReturned(returnedMessage);
//            correlationData.setId(uuid);
            // 最后一个参数设置指定uuid
//            rabbitTemplate.convertAndSend(RabbitmqExchangeName.EMAIL_CODE_EXCHANGE,
//                    CommonRabbitmqRoutingName.EMAIL_CODE, rabbitmqMessage,
//                    correlationData);
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.EMAIL_CODE_EXCHANGE,
                    RabbitmqRoutingName.EMAIL_CODE, msg);
            // 开启手动确认机制，只要队列的消息到消费端没有被确认，消息就一直是unacked状态，即使comsumer关机，消息不会丢失，会重新变为Ready状态


            return new ResultVO(ResultStatus.OK, "发送验证码成功，请登录邮箱查看", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO(ResultStatus.NO, "发生未知错误, 验证码发送失败", null);
        }
    }

    @Override
    public void getCaptcha(HttpServletResponse response) {
        //随机生成4位验证码
        RandomGenerator randomGenerator = new RandomGenerator("0123456789", 4);
        //定义图片的显示大小
        lineCaptcha = CaptchaUtil.createLineCaptcha(150, 30);
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "No-cache");
        try {
            //调用父类的setGenerator()方法，设置验证码的类型
            lineCaptcha.setGenerator(randomGenerator);
            //输出到页面
            lineCaptcha.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //最后进行IO流的关闭
                response.getOutputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用户邮箱验证码登录
     *
     * @param email 用户邮箱
     * @param verifyCode 用户验证码
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/9 20:57
     */
    @Override
    public ResultVO loginEmail(String email, String verifyCode) {
        if (!this.isQQEmail(email)) {
            return new ResultVO(ResultStatus.NO, "请输入正确的QQ邮箱格式", null);
        }

        String redisUrl = RedisKeyAndTimeEnum.VERFY_CODE_REGISTER.getKeyName() + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisUrl))) {
            String code = stringRedisTemplate.opsForValue().get(redisUrl);
            if (!verifyCode.equals(code)) {
                return new ResultVO(ResultStatus.NO, "验证错误，请重新输入", null);
            }
        } else {
            return new ResultVO(ResultStatus.NO, "验证已过期或错误，请重新输入", null);
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", email);
        User user = userMapper.selectOne(userQueryWrapper);
        String token = JwtUtil.createJWT(user.getId().toString());

        // 将用户信息存入redis
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + user.getId();
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(user));

        return new ResultVO(ResultStatus.OK, "登录成功", token);
    }

    /**
     * 退出登录
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/20 9:13
     */
    @Override
    public R logout() {
        String userId = JwtUtil.getUserId();
        if (userId == null || "".equals(userId)) {
            throw new MonkeyBlogException(ExceptionEnum.UN_LOGIN.getCode(), ExceptionEnum.UN_LOGIN.getMsg());
        }

        // 若用户存在删除redis中的数据
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + userId;
        stringRedisTemplate.delete(redisKey);

        return R.ok();
    }

    /**
     * 用户登录功能
     *
     * @param username
     * @param password
     * @param verifyCode
     * @return {@link null}
     * @author Anna.
     * @date 2023/7/6 8:59
     */
    @Override
    public ResultVO loginUsername(String username, String password, String verifyCode) {
        if (!verifyCode.equals(this.lineCaptcha.getCode())) {
            return new ResultVO(ResultStatus.NO, "验证码输入错误，请重新输入", null);
        }

        // 使用authenticationManager进行用户验证
        // authenticationManager.authenticate()
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        // 执行到此处说明认证通过了
        UserDetailsImpl userDetails = (UserDetailsImpl)authenticate.getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {
            return new ResultVO(ResultStatus.NO, "用户名不存在，请重新输入", null);
        } else {
            String authPassword = user.getPassword();
            if (!passwordEncoder.matches(password, authPassword)) {
                return new ResultVO(ResultStatus.NO, "密码错误，请重新输入", null);
            }
        }
        String token = JwtUtil.createJWT(user.getId().toString());
        // 将用户信息存入redis
        String redisKey = RedisKeyAndTimeEnum.USER_INFO.getKeyName() + user.getId();
        stringRedisTemplate.opsForValue().set(redisKey, JSONObject.toJSONString(userDetails));
        return new ResultVO(ResultStatus.OK, "登录成功", token);
    }


    // 得到位数为codeLength的验证码
    private String getVerifyCode(int codeLength) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < codeLength; i++) {
            int index = random.nextInt(characters.length());
            char character = characters.charAt(index);
            codeBuilder.append(character);
        }

        return codeBuilder.toString();
    }
}

