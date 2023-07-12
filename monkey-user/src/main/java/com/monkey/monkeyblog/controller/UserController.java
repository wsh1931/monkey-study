package com.monkey.monkeyblog.controller;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.pojo.Vo.RegisterVo;
import com.monkey.monkeyblog.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResultVO registerUser(@RequestParam Map<String, String> data) {
        RegisterVo registerVo = JSONObject.parseObject(data.get("userInformation"), RegisterVo.class);
        return userService.userRegister(registerVo);
    }

    // 通过用户名密码登录
    @PostMapping("/login/loginUsername")
    public ResultVO loginUsername(@RequestParam Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");
        String verifyCode = data.get("verifyCode");
        return userService.loginUsername(username, password, verifyCode);
    }

    // 通过邮箱验证码登录
    @PostMapping("/login/loginEmail")
    public ResultVO loginEmail(@RequestParam Map<String, String> data) {
        String email = data.get("email");
        String verifyCode = data.get("verifyCode");
        return userService.loginEmail(email, verifyCode);
    }

    @GetMapping("/getUserInfoBytoken")
    public ResultVO getUserInfoBytoken() {
        return userService.getUserInfoByToken();
    }

    // 发送验证码给对方QQ邮箱
    @PostMapping("/sendVerfyCode")
    public ResultVO sendVerfyCode(@RequestParam Map<String, String> data) {
        String targetEmail = data.get("targetEmail");
        String isRegister = data.get("isRegister");
        return userService.sendVerfyCode(targetEmail, isRegister);
    }

    // 生产验证码
    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletResponse response) {
        userService.getCaptcha(response);
    }
}
