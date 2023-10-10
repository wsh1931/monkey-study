package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-09 09:35:26
 */
@Data
@TableName("resource")
public class Resource implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 发布用户id
	 */
	private Long userId;
	/**
	 * 形式类型id
	 */
	private Long formTypeId;
	/**
	 * 资源地址
	 */
	private String url;
	/**
	 * 资源类型
	 */
	private String type;
	/**
	 * 资源名称
	 */
	private String name;
	/**
	 * 资源描述
	 */
	private String description;
	/**
	 * 资源游览数
	 */
	private Long viewCount;
	/**
	 * 资源总评分
	 */
	private BigDecimal score;
	/**
	 * 评分人数
	 */
	private Integer scoreCount;
	/**
	 * 评论人数
	 */
	private Integer commentCount;
	/**
	 * 收藏人数
	 */
	private Integer collectCount;

	private Integer downCount;
	/**
	 * 点赞数
	 */
	private Integer likeCount;
	/**
	 * 是否精选(0表示不精选，1表示精选)
	 */
	private Integer isCuration;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
