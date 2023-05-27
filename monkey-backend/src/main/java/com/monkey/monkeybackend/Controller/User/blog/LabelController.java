package com.monkey.monkeybackend.Controller.User.blog;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeybackend.Service.Blog.LabelService;
import com.monkey.monkeybackend.utils.result.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/blog/label")
public class LabelController {
    @Autowired
    private LabelService labelService;

    @GetMapping("/getLabelList")
    public ResultVO getLabelList() {
        return labelService.getLabelList();
    }
}
