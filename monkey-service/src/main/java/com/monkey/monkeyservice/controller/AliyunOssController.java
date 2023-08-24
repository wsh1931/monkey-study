package com.monkey.monkeyservice.controller;

import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyservice.service.AliyunOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/monkey-aliyun/aliyun/oss")
public class AliyunOssController {
    @Autowired
    private AliyunOssService aliyunOssService;

    // 上传文章封面的方法
    @PostMapping("/upload")
    public ResultVO uploadOssFile(@RequestParam("file") MultipartFile picture,
                                  @RequestParam("module") String module) {
        // 获取图片
        // 返回上传到阿里云oss的路径
        String url = aliyunOssService.uploadFile(picture, module);
        return new ResultVO(ResultStatus.OK, null, url);
    }

    // 删除文件的方法
    @DeleteMapping("/remove")
    public ResultVO removeFile(@RequestParam("fileUrl") String fileUrl) {
        return aliyunOssService.removeFile(fileUrl);
    }
}
