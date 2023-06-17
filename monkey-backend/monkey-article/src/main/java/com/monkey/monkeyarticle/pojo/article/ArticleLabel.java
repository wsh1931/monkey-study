package com.monkey.monkeyarticle.pojo.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleLabel {
    private Long id;
    private Long articleId;
    private Long labelId;
}
