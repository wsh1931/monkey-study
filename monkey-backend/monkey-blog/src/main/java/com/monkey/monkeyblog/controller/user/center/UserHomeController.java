package com.monkey.monkeyblog.controller.user.center;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.service.user.center.UserHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/home")
public class UserHomeController {

    @Autowired
    private UserHomeService userHomeService;

    public ResultVO getUserInformationByUserId(@RequestParam Map<String, String> data) {
        return userHomeService.getUserInformationByUserId(data);
    }
}
