package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 社区文章投票选项表(community_article_veto_item)
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("community_article_veto_item")
public class CommunityArticleVetoItem {
	/**
	 * 
	 */
	@TableId
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
	private Date createTime;

}
