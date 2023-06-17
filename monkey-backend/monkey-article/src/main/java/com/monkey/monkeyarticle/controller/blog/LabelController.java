package com.monkey.monkeyarticle.controller.blog;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.service.blog.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
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
