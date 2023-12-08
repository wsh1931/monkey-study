package com.monkey.monkeyblog.pojo.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/12/8 16:17
 * @version: 1.0
 * @description:
 */
@Data
public class HistoryLikeVo {
    /**
     * 主键id
     */
    private Long id;
    private Long userId;
    /**
     * 作者id
     */
    private Long authorId;
    private String authorName;
    private String authorHeadImg;
    /**
     * 游览类型(0表示文章，1表示问答，2表示课程，3表示社区文章, 4表示资源)
     */
    private Integer type;

    private String typeName;
    /**
     * 关联id(0表示文章，1表示问答，2表示课程，3表示社区文章, 4表示资源)
     */
    private Long associateId;
    private String title;
    private String picture;
    /**
     * 游览时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
}
