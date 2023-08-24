package com.monkey.monkeyservice.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyservice.service.AliyunVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/8/15 17:10
 * @version: 1.0
 * @description:
 */
@Api(tags = "阿里云视频上传")
@RestController
@RequestMapping("/monkey-aliyun/aliyun/video")
public class AliyunVideoController {

    @Autowired
    private AliyunVideoService aliyunVideoService;
    // 上传视频到阿里云
    @PostMapping("/uploadAliyunVideo")
    @ApiOperation("上传视频到阿里云")
    public R uploadAliyunVideo(@RequestPart("file") MultipartFile file) {
        // 上传之后的返回值为视频凭证id
        return aliyunVideoService.uploadAliyunVideo(file);
    }

    @GetMapping("/getVideoPlayUrl")
    @ApiOperation("得到视频播放地址")
    public R getVideoPlayUrl(@RequestParam Map<String, String> data) {
        String videoSourceId = data.get("videoSourceId");
        return aliyunVideoService.getVideoPlayUrl(videoSourceId);
    }

    @DeleteMapping("/deleteVideoPlayByVideoSourceId")
    @ApiOperation("删除视频播放地址")
    public R deleteVideoPlayByVideoSourceId(@RequestParam Map<String, String> data) {
        String videoSourceId = data.get("videoSourceId");
        return aliyunVideoService.deleteVideoPlayByVideoSourceId(videoSourceId);
    }
}
