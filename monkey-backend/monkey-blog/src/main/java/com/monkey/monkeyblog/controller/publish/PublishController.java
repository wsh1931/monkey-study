package com.monkey.monkeyblog.controller.publish;

import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.service.publish.PublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/publish")
public class PublishController {
    @Autowired
    private PublishService publishService;

    @PostMapping("/article")
    public ResultVO publishArticle(@RequestParam Map<String, String> data) {
        return publishService.publishArticle(data);
    }
}
