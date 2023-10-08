package com.monkey.monkeyresource.service;

import com.monkey.monkeyUtils.result.R;

public interface UploadResourceService {
    // 通过文件类型得到文件类型图片
    R queryFileTypeIcon(String fileType);
}
