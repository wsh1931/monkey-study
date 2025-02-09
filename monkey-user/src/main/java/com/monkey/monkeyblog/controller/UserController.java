package com.monkey.monkeyblog.controller;


import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.pojo.Vo.RegisterVo;
import com.monkey.monkeyblog.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/monkey-user/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation("注册用户")
    public ResultVO registerUser(@RequestParam Map<String, String> data) {
        RegisterVo registerVo = JSONObject.parseObject(data.get("userInformation"), RegisterVo.class);
        return userService.userRegister(registerVo);
    }

    @ApiOperation("通过用户名密码登录")
    @PostMapping("/login/loginUsername")
    public ResultVO loginUsername(@RequestParam Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");
        String verifyCode = data.get("verifyCode");
        return userService.loginUsername(username, password, verifyCode);
    }

    @ApiOperation("通过邮箱验证码登录")
    @PostMapping("/login/loginEmail")
    public ResultVO loginEmail(@RequestParam Map<String, String> data) {
        String email = data.get("email");
        String verifyCode = data.get("verifyCode");
        return userService.loginEmail(email, verifyCode);
    }

    @ApiOperation("退出登录")
    @PutMapping("/logout")
    public R logout() {
        return userService.logout();
    }

    @ApiOperation("通过token得到用户信息")
    @GetMapping("/getUserInfoBytoken")
    public ResultVO getUserInfoBytoken() {
        return userService.getUserInfoByToken();
    }

    @ApiOperation("发送验证码给对方QQ邮箱")
    @PostMapping("/sendVerfyCode")
    public ResultVO sendVerfyCode(@RequestParam Map<String, String> data) {
        String targetEmail = data.get("targetEmail");
        String isRegister = data.get("isRegister");
        return userService.sendVerfyCode(targetEmail, isRegister);
    }

    @ApiOperation("生成验证码")
    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletResponse response) {
        userService.getCaptcha(response);
    }
}
