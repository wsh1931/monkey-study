package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 社区文章任务表(community_article_task)
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
public class CommunityArticleTask {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 任务指定的用户集合id
	 */
	private String userIds;
	/**
	 * 社区文章id
	 */
	private Long communityArticleId;
	/**
	 * 完成任务获得的积分
	 */
	private Integer finishScore;
	/**
	 * 任务提交结果是否公开（0表示公开，1表示不公开）
	 */
	private Integer isPublic;
	/**
	 * 任务接取方式（0不指定用户接取，指定用户接取）
	 */
	private Integer receiverWay;
	/**
	 * 任务结束时间
	 */
	private Date endTime;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date updateTime;

}
