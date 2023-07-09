package com.monkey.monkeyblog.controller.user;


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

    @PostMapping("/login")
    public ResultVO userLogin(@RequestParam Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");
        return userService.userLogin(username, password);
    }

    @GetMapping("/getUserInfoBytoken")
    public ResultVO getUserInfoBytoken() {
        return userService.getUserInfoByToken();
    }

    // 发送验证码给对方QQ邮箱
    @PostMapping("/sendVerfyCode")
    public ResultVO sendVerfyCode(@RequestParam Map<String, String> data) {
        String targetEmail = data.get("targetEmail");
        return userService.sendVerfyCode(targetEmail);
    }

    // 生产验证码
    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletResponse response) {
        userService.getCaptcha(response);

    }

}
