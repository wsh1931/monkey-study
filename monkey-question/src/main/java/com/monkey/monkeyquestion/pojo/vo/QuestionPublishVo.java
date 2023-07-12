package com.monkey.monkeyquestion.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @description:
 * @date: 2023/7/12 11:16
 * @version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionPublishVo {
    private String title;
    private String profile;
    private List<Long> labelId = new ArrayList<>();
}
