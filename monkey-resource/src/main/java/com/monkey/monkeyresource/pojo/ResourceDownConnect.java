package com.monkey.monkeyresource.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资源下载关系表
 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-10-09 09:35:26
 */
@Data
@TableName("resource_down_connect")
public class ResourceDownConnect{

	/**
	 * 主键id
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 资源id
	 */
	private Long resourceId;
	/**
	 * 下载用户id
	 */
	private Long userId;
	/**
	 * 更新时间
	 */
	private Date createTime;

}
