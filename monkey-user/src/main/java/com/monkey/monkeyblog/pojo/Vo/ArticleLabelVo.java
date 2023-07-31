package com.monkey.monkeyblog.pojo.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: wusihao
 * @date: 2023/7/31 10:58
 * @version: 1.0
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLabelVo {
    private Long id;
    private Long articleId;
    private Long labelId;
}
