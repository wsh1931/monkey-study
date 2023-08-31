package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 社区表(community)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("community")
public class Community{
	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 学校id
	 */
	private Integer schoolId;
	/**
	 * 社区名称
	 */
	private String name;
	/**
	 * 社区描述
	 */
	private String description;
	/**
	 * 社区头像
	 */
	private String headimg;
	/**
	 * 社区分类
	 */
	private String classification;
	/**
	 * 评论前是否需要加入社区（0不需要，1需要）
	 */
	private Integer isComment;
	/**
	 * 属性标签
	 */
	private String attributeLabel;
	/**
	 * 社区公告
	 */
	private String notice;
	/**
	 * 加入社区方式(0表示无限制，1表示管理员邀请，2表示定向邀请)
	 */
	private Integer enterWay;
	/**
	 * 所有人是否都能看见（0表示可以，1表示只有社区内成员看见）
	 */
	private Integer innerCommunity;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
