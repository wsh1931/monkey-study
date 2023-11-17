package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.pojo.vo.UserFansVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.pojo.UserFans;
import com.monkey.monkeyblog.service.UserFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/7/20 15:39
 * @version: 1.0
 * @description:
 */
@Api(tags = "用户feign模块接口")
@RestController
@RequestMapping("/monkey-user/feign")
public class UserFeignContoller {
    @Resource
    private UserFeignService userFeignService;

    @ApiOperation("通过fans_id和user_id判断当前登录用户是否是对方粉丝")
    @GetMapping("/judgeLoginUserAndAuthorConnect")
    public R judgeLoginUserAndAuthorConnect(@RequestParam @ApiParam("用户id") Long userId,
                                            @RequestParam @ApiParam("粉丝id") Long fansId) {
        return userFeignService.judgeLoginUserAndAuthorConnect(userId, fansId);
    }

    @ApiOperation("通过用户id得到用户信息")
    @GetMapping("/getUserInfoByUserId/{userId}")
    public R getUserInfoByUserId(@PathVariable @ApiParam("用户id") Long userId) {
        return userFeignService.getUserInfoByUserId(userId);
    }

    @ApiOperation("得到userFans通过userId, 和fansId")
    @GetMapping("/getUserFansByUserAndAuthorConnect")
    public R getUserFansByUserAndAuthorConnect(@RequestParam @ApiParam("用户id") Long userId,
                                               @RequestParam @ApiParam("粉丝id") Long fansId) {
        return userFeignService.getUserFansByUserAndAuthorConnect(userId, fansId);
    }

    @ApiOperation("通过id删除userFans")
    @DeleteMapping("/deleteUserFans")
    public R deleteUserFansById(@RequestBody @ApiParam("用户粉丝关系表实体类") UserFans userFans) {
        return userFeignService.deleteUserFansById(userFans);
    }

    @ApiOperation("插入userFans")
    @PostMapping("/add/UserFans")
    public R addUserFans(@RequestBody @ApiParam("用户粉丝UserFansVo实体类") UserFansVo userFansVo) {
        return userFeignService.addUserFans(userFansVo);
    }

    @ApiOperation("通过用户id得到用户关注数和粉丝数")
    @GetMapping("/getUserConcernAndFansCountByUserId/{userId}")
    public R getUserConcernAndFansCountByUserId(@PathVariable @ApiParam("用户id") Long userId) {
        return userFeignService.getUserConcernAndFansCountByUserId(userId);
    }

}
