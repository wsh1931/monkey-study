package com.monkey.monkeyresource.pojo.vo;

import com.monkey.monkeyresource.pojo.ResourceClassification;
import com.monkey.monkeyresource.pojo.ResourceLabel;
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
public class ResourceVo {
    private Long id;
    private String url;
    private String type;
    private String name;
    private Long formTypeId;
    private String description;
    private List<ResourceClassification> resourceClassificationList = new ArrayList<>();
    private List<ResourceLabel> resourceLabelList = new ArrayList<>();
}
