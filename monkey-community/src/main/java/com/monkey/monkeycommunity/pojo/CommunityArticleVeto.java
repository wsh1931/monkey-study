package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 社区文章投票表(community_article_veto)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("community_article_veto")
public class CommunityArticleVeto {

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 社区文章id
	 */
	private Long communityArticleId;
	/**
	 * 
	 */
	private String vetoName;
	/**
	 * 投票时长（单位天）
	 */
	private Integer vetoDuration;
	/**
	 * 投票种类（0表示单选，1表示多选）
	 */
	private Integer vetoKind;
	/**
	 * 已投票人数
	 */
	private Integer vetoPeople;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
