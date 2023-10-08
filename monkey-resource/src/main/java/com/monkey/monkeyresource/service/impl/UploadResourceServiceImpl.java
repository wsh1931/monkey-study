package com.monkey.monkeyresource.service.impl;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.service.UploadResourceService;
import org.springframework.stereotype.Service;

/**
 * @author: wusihao
 * @date: 2023/10/7 17:48
 * @version: 1.0
 * @description:
 */
@Service
public class UploadResourceServiceImpl implements UploadResourceService {

    /**
     * 通过文件类型得到文件类型图片
     *
     * @param fileType 文件类型
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/8 10:52
     */
    @Override
    public R queryFileTypeIcon(String fileType) {
        FileTypeEnum fileUrlByFileType = FileTypeEnum.getFileUrlByFileType(fileType);
        return R.ok(fileUrlByFileType.getUrl());
    }
}
