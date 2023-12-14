package com.monkey.monkeyUtils.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 用户作品统计表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-12-09 11:16:20
 */
@Data
public class UserOpusStatistics {

	/**
	 * 主键 id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 作者id
	 */
	private Long authorId;
	private Integer opusCount;
	/**
	 * 阅读量
	 */
	private Integer viewCount;
	/**
	 * 点赞数
	 */
	private Integer likeCount;
	/**
	 * 
	 */
	private Integer collectCount;
	/**
	 * 评论次数
	 */
	private Integer commentCount;
	private Integer addFans;
	private Integer cancelFans;
	private Integer buyCount;
	private BigDecimal harvestMoney;
	private Integer downCount;
	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
	private Date createTime;

}
