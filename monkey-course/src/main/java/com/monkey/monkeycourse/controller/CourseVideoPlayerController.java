package com.monkey.monkeycourse.controller;

import com.monkey.monkeyUtils.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @author: wusihao
 * @date: 2023/8/10 14:19
 * @version: 1.0
 * @description:
 */
@RestController
@RequestMapping("/monkey-course/video/player")
public class CourseVideoPlayerController {

    @GetMapping("/testv3")
    public String test(@RequestParam String id) {
        System.err.println(id);
        return "测试";
    }
}
