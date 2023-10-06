package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

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
public class CommunityChannel {

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 社区id
	 */
	private Long communityId;
	private String channelName;
	private Long sort;
	private Integer supportManageModify;
	/**
	 * 是否支持前端展示（0支持，1不支持）
	 */
	private Integer supportShow;
	/**
	 * 是否支持用户发布文章（0表示支持，1表示不支持）
	 */
	private Integer supportUserPublish;
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
