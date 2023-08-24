package com.monkey.monkeyservice.service;

import com.monkey.monkeyUtils.result.ResultVO;
import org.springframework.web.multipart.MultipartFile;

public interface AliyunOssService {
    // 获取上传图片到阿里云OSS
    String uploadFile(MultipartFile file, String module);

    // 删除文件
    ResultVO removeFile(String fileUrl);
}
