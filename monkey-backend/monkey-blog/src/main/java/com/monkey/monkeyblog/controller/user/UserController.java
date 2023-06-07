package com.monkey.monkeyblog.controller.user;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResultVO registerUser(@RequestParam Map<String, String> data) {
        return userService.userRegister(data);
    }

    @PostMapping("/login")
    public ResultVO userLogin(@RequestParam Map<String, String> data) {
        return userService.userLogin(data);
    }

    @GetMapping("/getUserInfoBytoken")
    public ResultVO getUserInfoBytoken() {
        return userService.getUserInfoByToken();
    }
}
