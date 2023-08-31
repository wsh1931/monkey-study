package com.monkey.monkeycommunity.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 社区文章评论表(community_article_comment)

 * 
 * @author wusihao
 * @email 1931443283@qq.com
 * @date 2023-08-31 16:47:20
 */
@Data
@TableName("community_article_comment")
public class CommunityArticleComment {

	/**
	 * 
	 */
	@TableId
	private Long id;
	/**
	 * 发送人id
	 */
	private Long senderId;
	/**
	 * 回复人id
	 */
	private Long replyId;
	/**
	 * 社区文章表id
	 */
	private Long communityArticleId;
	/**
	 * 评论内容
	 */
	private String content;
	/**
	 * 父id(0表示一级评论)
	 */
	private Long parentId;
	/**
	 * 是否精选(0表示不精选，1表示精选)
	 */
	private String isCuration;
	/**
	 * 评论点赞数
	 */
	private Long likeCount;
	/**
	 * 评论时间
	 */
	private Date createTime;

}
