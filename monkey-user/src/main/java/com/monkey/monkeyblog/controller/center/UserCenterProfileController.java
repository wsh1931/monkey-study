package com.monkey.monkeyblog.controller.center;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.center.UserCenterProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/12/2 17:45
 * @version: 1.0
 * @description:
 */
@Api(tags = "个人中心-用户资料接口")
@RestController
@RequestMapping("/monkey-user/center/profile")
public class UserCenterProfileController {
    @Resource
    private UserCenterProfileService userCenterProfileService;

    @ApiOperation("查询用户信息")
    @GetMapping("/queryUserInfo")
    public R queryUserInfo() {
        return userCenterProfileService.queryUserInfo();
    }

    @ApiOperation("更新用户头像")
    @PutMapping("/updateUserHeadImg")
    public R updateUserHeadImg(@RequestParam("photo") @ApiParam("用户头像地址") String photo) {
        return userCenterProfileService.updateUserHeadImg(photo);
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/updateUserInfo")
    public R updateUserInfo(@RequestParam("userInfoStr") @ApiParam("用户信息实体类字符串") String userInfoStr) {
        User user = JSONObject.parseObject(userInfoStr, User.class);
        return userCenterProfileService.updateUserInfo(user);
    }
}
