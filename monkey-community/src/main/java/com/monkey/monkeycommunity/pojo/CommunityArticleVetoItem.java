package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 社区文章投票选项表(community_article_veto_item)
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
public class CommunityArticleVetoItem {
	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 社区文章投票id
	 */
	private Long communityArticleVetoId;
	/**
	 * 投票选项
	 */
	private String vetoItem;
	/**
	 * 票数
	 */
	private Integer poll;
	/**
	 * 投票时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createTime;

}
