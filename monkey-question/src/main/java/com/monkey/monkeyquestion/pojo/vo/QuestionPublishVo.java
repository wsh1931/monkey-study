package com.monkey.monkeyquestion.pojo.vo;

import com.monkey.monkeyUtils.pojo.Label;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
    private Long id;
    private String title;
    private String profile;
    private List<Long> labelId = new ArrayList<>();
    private List<Label> labelList = new ArrayList<>();
}
