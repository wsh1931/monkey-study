package com.monkey.monkeyoss.controller;

import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyoss.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.util.Arrays;

@CrossOrigin
@RestController
@RequestMapping("/aliyun/oss")
public class OssController {
    @Autowired
    private OssService ossService;

    // 上传文章封面的方法
    @PostMapping("/upload")
    public ResultVO uploadOssFile(@RequestParam("file") MultipartFile picture,
                                  @RequestParam("module") String module) {
        // 获取图片
        // 返回上传到阿里云oss的路径
        String url = ossService.uploadFile(picture, module);
        return new ResultVO(ResultStatus.OK, null, url);
    }

    // 删除文件的方法
    @DeleteMapping("/remove")
    public ResultVO removeFile(@RequestParam("fileUrl") String fileUrl) {
        return ossService.removeFile(fileUrl);
    }
}
