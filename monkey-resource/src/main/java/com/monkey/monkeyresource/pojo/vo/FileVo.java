package com.monkey.monkeyresource.pojo.vo;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @author: wusihao
 * @date: 2023/10/17 9:16
 * @version: 1.0
 * @description:
 */
@Data
public class FileVo {
    private String fileUrl;
    private String fileName;
    private Integer code;
    private String msg;
}
