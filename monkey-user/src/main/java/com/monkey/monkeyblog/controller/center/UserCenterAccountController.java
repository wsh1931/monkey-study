package com.monkey.monkeyblog.controller.center;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.center.UserCenterAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/12/3 15:45
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户中心-账号设置接口")
@RestController
@RequestMapping("/monkey-user/center/account")
public class UserCenterAccountController {
    @Resource
    private UserCenterAccountService userCenterAccountService;

    @ApiOperation("修改密码")
    @PutMapping("/modifyPassword")
    public R modifyPassword(@RequestParam("password") @ApiParam("密码") String password,
                            @RequestParam("confirmPassword") @ApiParam("确认密码") String confirmPassword) {
        return userCenterAccountService.modifyPassword(password, confirmPassword);
    }

    @ApiOperation("得到用户信息")
    @GetMapping("/queryUserInfo")
    public R queryUserInfo() {
        return userCenterAccountService.queryUserInfo();
    }

    @ApiOperation("得到用户邮箱")
    @GetMapping("/queryUserEmail")
    public R queryUserEmail() {
        return userCenterAccountService.queryUserEmail();
    }

    @ApiOperation("发送邮箱验证的验证码")
    @PostMapping("/sendEmailVerify")
    public R sendEmailVerify() {
        return userCenterAccountService.sendEmailVerify();
    }

    @ApiOperation("提交验证码")
    @PostMapping("/submitVerify")
    public R submitVerify(@RequestParam("verifyCode") @ApiParam("用户输入的验证码") String verifyCode) {
        return userCenterAccountService.submitVerify(verifyCode);
    }

    @ApiOperation("判断用户邮箱是否被验证")
    @GetMapping("/judgeUserEmailIsVerify")
    public R judgeUserEmailIsVerify() {
        return userCenterAccountService.judgeUserEmailIsVerify();
    }

    @ApiOperation("判断用户邮箱是否绑定")
    @GetMapping("/judgeUserEmailIsBind")
    public R judgeUserEmailIsBind() {
        return userCenterAccountService.judgeUserEmailIsBind();
    }

    @ApiOperation("发送邮箱绑定验证码")
    @PostMapping("/sendEmailBindVerify")
    public R sendEmailBindVerify(@RequestParam("email") @ApiParam("用户需要绑定的邮箱") String email) {
        return userCenterAccountService.sendEmailBindVerify(email);
    }

    @ApiOperation("提交绑定邮箱验证码")
    @PutMapping("/submitBindVerify")
    public R submitBindVerify(@RequestParam("verifyCode") @ApiParam("用户输入的验证码") String verifyCode,
                              @RequestParam("email") @ApiParam("用户需要绑定的邮箱") String email) {
        return userCenterAccountService.submitBindVerify(verifyCode, email);
    }
}
