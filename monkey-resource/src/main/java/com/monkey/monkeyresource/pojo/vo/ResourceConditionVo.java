package com.monkey.monkeyresource.pojo.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/12/19 20:41
 * @version: 1.0
 * @description:
 */
@Data
public class ResourceConditionVo {
    private Long currentPage;
    private Integer pageSize;
    private Long formTypeId;
    private String type;
    private Integer status;
    private String name;
    private List<Long> resourceClassification;
    private List<Date> dateList;

}
