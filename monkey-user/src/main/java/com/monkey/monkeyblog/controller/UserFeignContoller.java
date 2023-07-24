package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.pojo.Vo.UserFansVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.pojo.UserFans;
import com.monkey.monkeyblog.service.UserFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: wusihao
 * @date: 2023/7/20 15:39
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-user/feign")
public class UserFeignContoller {
    @Autowired
    private UserFeignService userFeignService;

    // 通过fans_id和user_id判断当前登录用户是否是对方粉丝
    @GetMapping("/judgeLoginUserAndAuthorConnect")
    public R judgeLoginUserAndAuthorConnect(@RequestParam Long userId, @RequestParam Long fansId) {
        return userFeignService.judgeLoginUserAndAuthorConnect(userId, fansId);
    }

    @GetMapping("/getUserInfoByUserId/{userId}")
    public R getUserInfoByUserId(@PathVariable Long userId) {
        return userFeignService.getUserInfoByUserId(userId);
    }

    // 得到userFans通过userId, 和fansId
    @GetMapping("/getUserFansByUserAndAuthorConnect")
    public R getUserFansByUserAndAuthorConnect(@RequestParam Long userId, @RequestParam Long fansId) {
        return userFeignService.getUserFansByUserAndAuthorConnect(userId, fansId);
    }

    // 通过id删除userFans
    @DeleteMapping("/deleteUserFansById/{userFansId}")
    public R deleteUserFansById(@PathVariable Long userFansId) {
        return userFeignService.deleteUserFansById(userFansId);
    }

    // 插入userFans
    @PostMapping("/add/UserFans")
    public R addUserFans(@RequestBody UserFansVo userFansVo) {
        return userFeignService.addUserFans(userFansVo);
    }
}
