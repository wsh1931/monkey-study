package com.monkey.monkeyservice.service;

import com.monkey.monkeyUtils.result.R;
import org.springframework.web.multipart.MultipartFile;

public interface AliyunVideoService {

    // 上传视频到阿里云
    R uploadAliyunVideo(MultipartFile file);


    R getVideoPlayUrl(String videoSourceId);

    // 删除视频播放地址
    R deleteVideoPlayByVideoSourceId(String videoSourceId);
}
