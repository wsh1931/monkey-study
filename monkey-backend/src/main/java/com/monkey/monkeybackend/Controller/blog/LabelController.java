package com.monkey.monkeybackend.Controller.blog;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeybackend.Service.blog.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/blog")
public class LabelController {
    @Autowired
    private LabelService labelService;

    @GetMapping("/getLabelList")
    public JSONObject getLabelList() {
        return labelService.getLabelList();
    }
}
