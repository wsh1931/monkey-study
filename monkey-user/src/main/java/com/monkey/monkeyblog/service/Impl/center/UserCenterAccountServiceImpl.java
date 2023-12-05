package com.monkey.monkeyblog.service.Impl.center;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.email.EmailContentConstant;
import com.monkey.monkeyUtils.email.EmailTitleConstant;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.UserMapper;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyUtils.util.CommonMethods;
import com.monkey.monkeyblog.constant.TipConstant;
import com.monkey.monkeyblog.mapper.EmailCodeMapper;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.redis.RedisKeyAndExpireEnum;
import com.monkey.monkeyblog.service.center.UserCenterAccountService;
import com.monkey.monkeyblog.util.UserCommonMethods;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.bind.util.JAXBSource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: wusihao
 * @date: 2023/12/3 15:59
 * @version: 1.0
 * @description:
 */
@Service
public class UserCenterAccountServiceImpl implements UserCenterAccountService {
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserMapper userMapper;

    @Value("${spring.mail.username}")
    private String sender;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private JavaMailSender mailSender;

    /**
     * 修改密码
     *
     * @param password 用户密码
     * @param confirmPassword 确认密码
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/3 16:03
     */
    @Override
    public R modifyPassword(String password, String confirmPassword) {
        if (password == null || "".equals(password)) {
            return R.error(null, "密码不能为空");
        }
        if (confirmPassword == null || "".equals(confirmPassword)) {
            return R.error(null, "确认密码不能为空");
        }
        if (password.length() < 1 || password.length() > 20) {
            return R.error(null, "密码长度必需在 1 到 20 个字符之间");
        }
        if (confirmPassword.length() < 1 || confirmPassword.length() > 20) {
            return R.error(null, "确认密码长度必需在 1 到 20 个字符之间");
        }
        if (!password.equals(confirmPassword)) {
            return R.error(null, "两次输入的密码不一样，请重新输入");
        }

        String encode = passwordEncoder.encode(password);
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        String userId = JwtUtil.getUserId();
        userLambdaUpdateWrapper.eq(User::getId, userId);
        userLambdaUpdateWrapper.set(User::getPassword, encode);
        userMapper.update(null, userLambdaUpdateWrapper);
        return R.ok();
    }

    /**
     * 得到用户信息
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/3 16:40
     */
    @Override
    public R queryUserInfo() {
        String userId = JwtUtil.getUserId();
        User user = userMapper.selectById(userId);
        user.setPassword(null);
        String phone = user.getPhone();
        Integer phoneVerified = user.getPhoneVerified();
        // 判断手机是否被验证
        if (CommonEnum.PHONE_IS_VERIFY.getCode().equals(phoneVerified)) {
            StringBuilder newPhone = new StringBuilder(phone.length());
            for (int i = 0; i < phone.length(); i ++ ) {
                if (i >= 3 && i <= 7) {
                    newPhone.append("*");
                } else {
                    newPhone.append(phone.charAt(i));
                }
            }
            user.setPhone(newPhone.toString());
        }

        // 判断QQ用户邮箱是否被验证
        Integer emailVerified = user.getEmailVerified();
        if (CommonEnum.EMAIL_IS_VERIFY.getCode().equals(emailVerified)) {
            String email = user.getEmail();
            user.setEmail(UserCommonMethods.getEncryptionEmail(email));
        }

        return R.ok(user);
    }

    /**
     * 得到用户邮箱
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/3 20:48
     */
    @Override
    public R queryUserEmail() {
        String userId = JwtUtil.getUserId();
        User user = userMapper.selectById(userId);
        user.setPassword(null);

        // 判断QQ用户邮箱是否被验证
        Integer emailVerified = user.getEmailVerified();
        if (CommonEnum.EMAIL_IS_VERIFY.getCode().equals(emailVerified)) {
            String email = user.getEmail();
            return R.ok(UserCommonMethods.getEncryptionEmail(email));
        }

        return R.error(TipConstant.unVerifyEmail);
    }

    /**
     * 发送邮箱验证的验证码
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/3 21:04
     */
    @Override
    public R sendEmailVerify() {
        String userId = JwtUtil.getUserId();
        User user = userMapper.selectById(userId);
        String email = user.getEmail();
        String redisKey = RedisKeyAndExpireEnum.UPDATE_EMAIL_VERIFY.getKeyName() + userId;
        // 防止用户在10分钟内多次发送验证码
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.error(null, TipConstant.emailVerifySendSuccess);
        }

        // 发送邮箱
        try {
            String verifyCode = UserCommonMethods.getVerifyCode(4);
            SimpleMailMessage message = new SimpleMailMessage();
            // 发送者
            message.setFrom(this.sender);
            // 接收者，可设置多个接收者
            message.setTo(email);
            // 文章标题
            String title = EmailTitleConstant.modifyEmail;
            message.setSubject(title);
            String content = "亲爱的用户 "+ user.getUsername() + "：您好！\n" +
                    "\n" +
                    "    您收到这封电子邮件是因为您 (也可能是某人冒充您的名义) 申请了修改邮箱。假如这不是您本人所申请, 请不用理会这封电子邮件, 但是如果您持续收到这类的信件骚扰, 请您尽快联络管理员。\n" +
                    "\n" +
                    "   请使用以下验证码完成后续修改邮箱流程\n" +
                    "   \n" +
                    "    "+ verifyCode + "\n" +
                    "  \n" +
                    "    注意:请您在收到邮件10分钟内使用，否则该验证码将会失效。";
            message.setText(content);
            mailSender.send(message);

            // 将验证码存入redis
            stringRedisTemplate.opsForValue().set(redisKey, verifyCode);
            stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.UPDATE_EMAIL_VERIFY.getTimeUnit(), TimeUnit.MINUTES);

            // 插入邮箱验证码发送表
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", EventConstant.insertEmailCode);
            jsonObject.put("senderEmail", this.sender);
            jsonObject.put("receiverEmail", email);
            jsonObject.put("emailContent", content);
            jsonObject.put("emailTitle", title);
            jsonObject.put("verity", verifyCode);
            jsonObject.put("createTime", new Date());
            Message message1 = new Message(jsonObject.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.userInsertDirectExchange,
                    RabbitmqRoutingName.userInsertRouting, message1);
            return R.ok(null, TipConstant.emailVerifySendSuccess);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 提交验证码
     *
     * @param verifyCode 用户提交的验证码
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/4 9:10
     */
    @Override
    public R submitVerify(String verifyCode) {
        if (verifyCode == null || "".equals(verifyCode)) {
            return R.error(TipConstant.errorEmail);
        }
        String userId = JwtUtil.getUserId();
        // 判断用户验证码是否过期
        String redisKey = RedisKeyAndExpireEnum.UPDATE_EMAIL_VERIFY.getKeyName() + userId;
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.error(TipConstant.emailNotVerifyOrExpire);
        }

        // 判断用户验证码是否正确
        String code = stringRedisTemplate.opsForValue().get(redisKey);
        if (!verifyCode.equals(code)) {
            return R.error(TipConstant.verifyError);
        }

        // 存入用户邮箱验证信息
        String redisEmailVerifyKey = RedisKeyAndExpireEnum.USER_EMAIL_VERIFY_SUCCESS.getKeyName() + userId;
        stringRedisTemplate.opsForValue().set(redisEmailVerifyKey, userId);
        stringRedisTemplate.expire(redisEmailVerifyKey, RedisKeyAndExpireEnum.USER_EMAIL_VERIFY_SUCCESS.getTimeUnit(), TimeUnit.MINUTES);
        return R.ok();
    }

    /**
     * 判断用户邮箱是否被验证, 或是否绑定邮箱
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/4 9:54
     */
    @Override
    public R judgeUserEmailIsVerify() {
        // 判断用户是否绑定邮箱
        String userId = JwtUtil.getUserId();
        User user = userMapper.selectById(userId);
        Integer emailVerified = user.getEmailVerified();
        if (!emailVerified.equals(CommonEnum.EMAIL_IS_VERIFY.getCode())) {
            return R.ok();
        }
        // 判断用户是否完成邮箱验证
        String redisKey = RedisKeyAndExpireEnum.USER_EMAIL_VERIFY_SUCCESS.getKeyName() + userId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.ok();
        }
        return R.error();
    }

    /**
     * 判断用户邮箱是否绑定
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/4 10:07
     */
    @Override
    public R judgeUserEmailIsBind() {
        String userId = JwtUtil.getUserId();
        User user = userMapper.selectById(userId);
        Integer emailVerified = user.getEmailVerified();
        if (emailVerified.equals(CommonEnum.EMAIL_IS_VERIFY.getCode())) {
            return R.ok();
        }
        return R.error();
    }

    /**
     * 发送邮箱绑定验证码
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/4 11:00
     */
    @Override
    public R sendEmailBindVerify(String email) {
        // 判断邮箱是否正确
        if (!UserCommonMethods.isQQEmail(email)) {
            return R.error(TipConstant.errorEmail);
        }
        String userId = JwtUtil.getUserId();
        String redisKey = RedisKeyAndExpireEnum.USER_BIND_EMAIL_VERIFY.getKeyName() + userId;
        // 防止用户在10分钟内多次发送验证码
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.error(null, TipConstant.emailVerifySendSuccess);
        }

        // 判断邮箱是否绑定
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getEmail, email);
        Long selectCount = userMapper.selectCount(userLambdaQueryWrapper);
        if (selectCount > 0) {
            return R.error(TipConstant.emailAlreadyExist);
        }

        // 发送邮箱
        try {
            String verifyCode = UserCommonMethods.getVerifyCode(4);
            SimpleMailMessage message = new SimpleMailMessage();
            // 发送者
            message.setFrom(this.sender);
            // 接收者，可设置多个接收者
            message.setTo(email);
            // 文章标题
            String title = EmailTitleConstant.bingEmail;
            message.setSubject(title);
            String content = "亲爱的用户 "+ userId + "：您好！\n" +
                    "\n" +
                    "    您收到这封电子邮件是因为您 (也可能是某人冒充您的名义) 申请了修改邮箱。假如这不是您本人所申请, 请不用理会这封电子邮件, 但是如果您持续收到这类的信件骚扰, 请您尽快联络管理员。\n" +
                    "\n" +
                    "   请使用以下验证码完成后续绑定邮箱流程\n" +
                    "   \n" +
                    "    "+ verifyCode + "\n" +
                    "  \n" +
                    "    注意:请您在收到邮件10分钟内使用，否则该验证码将会失效。";
            message.setText(content);
            mailSender.send(message);

            // 将验证码存入redis
            JSONObject data = new JSONObject();
            data.put("email", email);
            data.put("verifyCode", verifyCode);
            stringRedisTemplate.opsForValue().set(redisKey, data.toJSONString());
            stringRedisTemplate.expire(redisKey, RedisKeyAndExpireEnum.USER_BIND_EMAIL_VERIFY.getTimeUnit(), TimeUnit.MINUTES);

            // 插入邮箱验证码发送表
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", EventConstant.insertEmailCode);
            jsonObject.put("senderEmail", this.sender);
            jsonObject.put("receiverEmail", email);
            jsonObject.put("emailContent", content);
            jsonObject.put("emailTitle", title);
            jsonObject.put("verity", verifyCode);
            jsonObject.put("createTime", new Date());
            Message message1 = new Message(jsonObject.toJSONString().getBytes());
            rabbitTemplate.convertAndSend(RabbitmqExchangeName.userInsertDirectExchange,
                    RabbitmqRoutingName.userInsertRouting, message1);
            return R.ok(null, TipConstant.emailVerifySendSuccess);
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 提交绑定邮箱验证码
     *
     * @param verifyCode 用户输入的验证码
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/4 11:27
     */
    @Override
    public R submitBindVerify(String verifyCode, String email) {
        // 判断邮箱是否正确
        if (!UserCommonMethods.isQQEmail(email)) {
            return R.error(TipConstant.errorEmail);
        }

        String userId = JwtUtil.getUserId();
        String redisKey = RedisKeyAndExpireEnum.USER_BIND_EMAIL_VERIFY.getKeyName() + userId;

        // 判断用户是否获取过验证码
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(redisKey))) {
            return R.error(TipConstant.unGetVerifyCode);
        }
        // 判断用户输入的验证码与邮箱与之前输入的是否相同
        String data = stringRedisTemplate.opsForValue().get(redisKey);
        JSONObject jsonObject = JSONObject.parseObject(data, JSONObject.class);
        String beforeCode = jsonObject.getString("verifyCode");
        String beforeEmail = jsonObject.getString("email");
        if (!verifyCode.equals(beforeCode)) {
            return R.error(TipConstant.verifyError);
        }

        if (!email.equals(beforeEmail)) {
            return R.error(TipConstant.emailError);
        }

        // 更新用户邮箱信息
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId, userId);
        userLambdaUpdateWrapper.set(User::getEmail, email);
        userMapper.update(null, userLambdaUpdateWrapper);

        // 删除用户邮箱验证码
        stringRedisTemplate.delete(RedisKeyAndExpireEnum.UPDATE_EMAIL_VERIFY.getKeyName() + userId);
        // 删除用户获得的授权信息
        stringRedisTemplate.delete(RedisKeyAndExpireEnum.USER_EMAIL_VERIFY_SUCCESS.getKeyName() + userId);
        // 删除用户绑定邮箱验证
        stringRedisTemplate.delete(redisKey);
        return R.ok();
    }
}