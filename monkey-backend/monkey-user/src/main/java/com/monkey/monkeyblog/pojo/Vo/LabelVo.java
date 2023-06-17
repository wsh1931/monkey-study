package com.monkey.monkeyblog.pojo.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelVo {
    private Long id;
    private String labelName;
    private Long articleCount;
}
