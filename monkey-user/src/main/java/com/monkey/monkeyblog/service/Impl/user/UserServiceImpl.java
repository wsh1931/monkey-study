package com.monkey.monkeyblog.service.Impl.user;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.email.EmailContentConstant;
import com.monkey.monkeyUtils.email.EmailTitleConstant;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyUtils.rabbitmq.RabbitmqRoutingKeyName;
import com.monkey.monkeyUtils.redis.RedisKeyConstant;
import com.monkey.monkeyUtils.redis.RedisTimeConstant;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.pojo.Vo.EmailCodeVo;
import com.monkey.monkeyblog.pojo.Vo.RegisterVo;
import com.monkey.monkeyblog.service.user.UserService;

import com.monkey.spring_security.JwtUtil;
import com.monkey.spring_security.mapper.user.UserMapper;
import com.monkey.spring_security.pojo.user.User;
import com.monkey.spring_security.user.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
        String redisKey = RedisKeyConstant.REGISTER_VERFY_CODE + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            RegisterVo registerVoCode = JSONObject.parseObject((String)redisTemplate.opsForValue().get(redisKey), RegisterVo.class) ;
            String verifyCodeBefore = registerVoCode.getVerifyCode();
            verifyCodeBefore = verifyCodeBefore.toLowerCase();
            String emailBefore = registerVoCode.getEmail();
            if (!emailBefore.equals(email)) {
                return new ResultVO(ResultStatus.NO, "邮箱发生变动，请重试", null);
            }
            if (!verifyCodeBefore.equals(verifyCode)) {
                return new ResultVO(ResultStatus.NO, "验证码错误，请重试", null);
            }
            Boolean delete = redisTemplate.delete(redisKey);
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

        QueryWrapper queryWrapper = new QueryWrapper<>();
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
    /**
     * 用户登录功能
     *
     * @param username
     * @param password
     * @return {@link null}
     * @author Anna.
     * @date 2023/7/6 8:59
     */
    @Override
    public ResultVO userLogin(String username, String password) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = // 将用户名与密码封装成一个加密之后的字符串
                new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken); // 登录失败自动处理

        UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();
        User user = userDetails.getUser();

        String token = JwtUtil.createJWT(user.getId().toString());

        return new ResultVO(ResultStatus.OK, "登录成功", token);
    }

    // 通过token得到用户信息
    @Override
    public ResultVO getUserInfoByToken() {
        // 从token中获取用户名与密码
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
     * @return {@link null}
     * @author Anna.
     * @date 2023/7/6 9:00
     */
    @Override
    public ResultVO sendVerfyCode(String targetEmail) {
        if (!this.isQQEmail(targetEmail)) {
            return new ResultVO(ResultStatus.NO, "邮箱输入错误，请确定输入的是QQ邮箱.", null);
        }

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("email", targetEmail);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user != null) {
            return new ResultVO(ResultStatus.NO, "该邮箱已被注册过，请重新输入", null);
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
            mailSender.send(message);
            redisTemplate.opsForValue().set(RedisKeyConstant.REGISTER_VERFY_CODE + targetEmail, verifyCode);
            redisTemplate.expire(RedisKeyConstant.REGISTER_VERFY_CODE + targetEmail, 5, TimeUnit.MINUTES);
            // 将信息放入消息队列中插入数据库
            String uuid = UUID.randomUUID().toString();
            EmailCodeVo emailCodeVo = new EmailCodeVo();
            emailCodeVo.setSenderEmail(this.sender);
            emailCodeVo.setEmailTitle(EmailTitleConstant.REGISTER_VERFY_CODE_TITLE);
            emailCodeVo.setEmailContent(content);
            emailCodeVo.setCreateTime(new Date());
            emailCodeVo.setReceiverEmail(targetEmail);
            emailCodeVo.setTryCount(0);
            String str = JSON.toJSONString(emailCodeVo);

            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setReceivedExchange(RabbitmqExchangeName.EMAIL_CODE_EXCHANGE);
            messageProperties.setReceivedRoutingKey(RabbitmqRoutingKeyName.EMAIL_CODE);
            messageProperties.setReceivedUserId(uuid);

            Message msg = new Message(str.getBytes(), messageProperties);
            Message rabbitmqMessage = new Message(str.getBytes(), messageProperties);
            CorrelationData correlationData = new CorrelationData();
            ReturnedMessage returnedMessage = new ReturnedMessage(msg,
                    200,
                    "邮箱验证码",
                    RabbitmqExchangeName.EMAIL_CODE_EXCHANGE,
                    RabbitmqRoutingKeyName.EMAIL_CODE);

            correlationData.setReturned(returnedMessage);
            correlationData.setId(uuid);
            // 最后一个参数设置指定uuid
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.EMAIL_CODE_EXCHANGE,
                    RabbitmqRoutingKeyName.EMAIL_CODE, rabbitmqMessage,
                    correlationData);
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
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(150, 30);
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

