package com.monkey.monkeyresource.pojo.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/10 9:09
 * @version: 1.0
 * @description:
 */
@Data
public class UploadResourcesVo {
    private Long id;
    private Long userId;
    private String url;
    private String type;
    private String name;
    private Long formTypeId;
    private String description;
    private Float price;
    private String typeUrl;
    private List<Long> resourceClassification = new ArrayList<>();
    private List<String> resourceLabelList = new ArrayList<>();

}
