package com.monkey.monkeyresource.pojo.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/10/14 17:01
 * @version: 1.0
 * @description:
 */
@Data
public class ResourceClassificationVo {
    private Long value;
    private String label;
    private List<ResourceClassificationVoChild> children = new ArrayList<>();
}
