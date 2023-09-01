package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 社区频道关系表(community_channel_connect)
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("community_channel_connect")
public class CommunityChannelConnect {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 社区id
	 */
	private Long communityId;
	/**
	 * 频道id
	 */
	private Long channelId;
	/**
	 * 是否支持前端展示（0支持，1不支持）
	 */
	private Integer supportShow;
	/**
	 * 是否支持用户发布文章（0表示支持，1表示不支持）
	 */
	private Integer supportUserPublish;
	/**
	 * 帖子是否支持在全部频道展示（0表示支持，1表示不支持）
	 */
	private Integer supportAllChannel;
	/**
	 * 该频道帖子排序规则（0最新发布，1最新评论，2阅读量最多，3收藏最多，4评分最多）
	 */
	private Integer sort;
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date createTime;
	/**
	 * 更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
	private Date updateTime;

}
