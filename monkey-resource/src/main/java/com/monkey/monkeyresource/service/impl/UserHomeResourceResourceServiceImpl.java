package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.*;
import com.monkey.monkeyUtils.mapper.*;
import com.monkey.monkeyUtils.pojo.*;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.constant.TipConstant;
import com.monkey.monkeyresource.feign.ResourceToSearchFeign;
import com.monkey.monkeyresource.mapper.*;
import com.monkey.monkeyresource.pojo.*;
import com.monkey.monkeyresource.pojo.vo.ResourcesVo;
import com.monkey.monkeyresource.pojo.vo.UploadResourcesVo;
import com.monkey.monkeyresource.rabbitmq.EventConstant;
import com.monkey.monkeyresource.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyresource.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyresource.service.UserHomeResourceService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/23 22:38
 * @version: 1.0
 * @description:
 */
@Service
public class UserHomeResourceResourceServiceImpl implements UserHomeResourceService {
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceConnectMapper resourceConnectMapper;

    @Resource
    private ResourceChargeMapper resourceChargeMapper;
    @Resource
    private ResourceLikeMapper resourceLikeMapper;
    @Resource
    private ResourceScoreMapper resourceScoreMapper;
    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;

    @Resource
    private ResourceCommentMapper resourceCommentMapper;

    @Resource
    private ResourceCommentLikeMapper resourceCommentLikeMapper;

    @Resource
    private ResourceToSearchFeign resourceToSearchFeign;
    @Resource
    private MessageCommentReplyMapper messageCommentReplyMapper;
    @Resource
    private MessageLikeMapper messageLikeMapper;
    @Resource
    private MessageCollectMapper messageCollectMapper;
    @Resource
    private ReportContentMapper reportContentMapper;
    @Resource private ReportCommentMapper reportCommentMapper;

    /**
     * 通过用户id查询资源集合
     *
     * @param userId  用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/24 8:56
     */
    @Override
    public R queryResourceByUserId(Long userId, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<Resources> resourcesLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourcesLambdaQueryWrapper.eq(Resources::getStatus, ResourcesEnum.SUCCESS.getCode());
        resourcesLambdaQueryWrapper.eq(Resources::getUserId, userId);
        resourcesLambdaQueryWrapper.orderByDesc(Resources::getCreateTime);
        Page page = new Page<>(currentPage, pageSize);
        Page selectPage = resourcesMapper.selectPage(page, resourcesLambdaQueryWrapper);
        List<Resources> records = selectPage.getRecords();
        List<ResourcesVo> resourcesVoList = new ArrayList<>();
        records.stream().forEach(resource -> {
            ResourcesVo resourcesVo = new ResourcesVo();
            BeanUtils.copyProperties(resource, resourcesVo);

            // 得到资源类型
            LambdaQueryWrapper<ResourceConnect> resourceConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourceConnectLambdaQueryWrapper.eq(ResourceConnect::getResourceId, resource.getId());
            resourceConnectLambdaQueryWrapper.eq(ResourceConnect::getLevel, CommonEnum.LABEL_LEVEL_TWO.getCode());
            resourceConnectLambdaQueryWrapper.select(ResourceConnect::getFormTypeId, ResourceConnect::getType);
            ResourceConnect resourceConnect = resourceConnectMapper.selectOne(resourceConnectLambdaQueryWrapper);

            resourcesVo.setFormTypeName(FormTypeEnum.getFormTypeEnum(resourceConnect.getFormTypeId()).getMsg());
            resourcesVo.setTypeUrl(FileTypeEnum.getFileUrlByFileType(resourceConnect.getType()).getUrl());
            resourcesVoList.add(resourcesVo);
        });

        selectPage.setRecords(resourcesVoList);
        return R.ok(selectPage);
    }

    /**
     * 删除资源
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/24 15:16
     */
    @Override
    @Transactional
    public R deleteResource(Long resourceId) {
        resourcesMapper.deleteById(resourceId);

        // 删除资源分类关系表
        LambdaQueryWrapper<ResourceConnect> resourceConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceConnectLambdaQueryWrapper.eq(ResourceConnect::getResourceId, resourceId);
        resourceConnectMapper.delete(resourceConnectLambdaQueryWrapper);


        // 删除资源点赞表
        LambdaQueryWrapper<ResourceLike> resourceLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceLikeLambdaQueryWrapper.eq(ResourceLike::getResourceId, resourceId);
        resourceLikeMapper.delete(resourceLikeLambdaQueryWrapper);

        // 删除资源收费表
        LambdaQueryWrapper<ResourceCharge> resourceChargeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceChargeLambdaQueryWrapper.eq(ResourceCharge::getResourceId, resourceId);
        resourceChargeMapper.delete(resourceChargeLambdaQueryWrapper);

        // 删除资源评分表
        LambdaQueryWrapper<ResourceScore> resourceScoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceScoreLambdaQueryWrapper.eq(ResourceScore::getResourceId, resourceId);
        resourceScoreMapper.delete(resourceScoreLambdaQueryWrapper);

        // 得到资源评论信息
        LambdaQueryWrapper<ResourceComment> resourceCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceCommentLambdaQueryWrapper.eq(ResourceComment::getResourceId, resourceId);
        resourceCommentLambdaQueryWrapper.select(ResourceComment::getId);
        List<Object> commentId = resourceCommentMapper.selectObjs(resourceCommentLambdaQueryWrapper);
        if (commentId != null && commentId.size() > 0) {
            // 删除资源评论信息
            LambdaQueryWrapper<ResourceComment> deleteResourceCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            deleteResourceCommentLambdaQueryWrapper.eq(ResourceComment::getId, commentId);
            resourceCommentMapper.delete(deleteResourceCommentLambdaQueryWrapper);
            // 得到资源评论点赞id列表
            LambdaQueryWrapper<ResourceCommentLike> resourceCommentLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourceCommentLikeLambdaQueryWrapper.in(ResourceCommentLike::getResourceCommentId, commentId);
            resourceCommentLikeLambdaQueryWrapper.select(ResourceCommentLike::getId);
            resourceCommentLikeMapper.delete(resourceCommentLikeLambdaQueryWrapper);

            // 删除消息回复评论表
            LambdaQueryWrapper<MessageCommentReply> messageCommentReplyLambdaQueryWrapper = new LambdaQueryWrapper<>();
            messageCommentReplyLambdaQueryWrapper.eq(MessageCommentReply::getAssociationId, resourceId);
            messageCommentReplyLambdaQueryWrapper.in(MessageCommentReply::getId, commentId);
            messageCommentReplyLambdaQueryWrapper.eq(MessageCommentReply::getType, ReportCommentEnum.RESOURCE_REPORT.getCode());
            messageCommentReplyMapper.delete(messageCommentReplyLambdaQueryWrapper);

            // 删除举报评论表
            LambdaQueryWrapper<ReportComment> reportCommentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            reportCommentLambdaQueryWrapper.eq(ReportComment::getType, ReportCommentEnum.RESOURCE_REPORT.getCode());
            reportCommentLambdaQueryWrapper.in(ReportComment::getAssociateId, commentId);
            reportCommentMapper.delete(reportCommentLambdaQueryWrapper);

            // 删除消息评论点赞表
            LambdaQueryWrapper<MessageLike> messageCommentLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            messageCommentLikeLambdaQueryWrapper.eq(MessageLike::getAssociationId, resourceId);
            messageCommentLikeLambdaQueryWrapper.eq(MessageLike::getType, MessageEnum.RESOURCE_MESSAGE.getCode());
            messageCommentLikeLambdaQueryWrapper.in(MessageLike::getCommentId, commentId);
            messageCommentLikeLambdaQueryWrapper.eq(MessageLike::getIsComment, CommonEnum.MESSAGE_LIKE_IS_COMMENT.getCode());
            messageLikeMapper.delete(messageCommentLikeLambdaQueryWrapper);
        }

        // 删除消息点赞表
        LambdaQueryWrapper<MessageLike> messageLikeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageLikeLambdaQueryWrapper.eq(MessageLike::getAssociationId, resourceId);
        messageLikeLambdaQueryWrapper.eq(MessageLike::getType, MessageEnum.RESOURCE_MESSAGE.getCode());
        messageLikeLambdaQueryWrapper.eq(MessageLike::getIsComment, CommonEnum.MESSAGE_LIKE_IS_CONTENT.getCode());
        messageLikeMapper.delete(messageLikeLambdaQueryWrapper);

        // 删除消息收藏表
        LambdaQueryWrapper<MessageCollect> messageCollectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageCollectLambdaQueryWrapper.eq(MessageCollect::getAssociationId, resourceId);
        messageCollectLambdaQueryWrapper.eq(MessageCollect::getType, MessageEnum.RESOURCE_MESSAGE.getCode());
        messageCollectMapper.delete(messageCollectLambdaQueryWrapper);

        // 删除举报内容表
        LambdaQueryWrapper<ReportContent> reportContentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportContentLambdaQueryWrapper.eq(ReportContent::getAssociateId, resourceId);
        reportContentLambdaQueryWrapper.eq(ReportContent::getType, ReportContentEnum.RESOURCE_REPORT.getCode());
        reportContentMapper.delete(reportContentLambdaQueryWrapper);

        // 删除收藏目录关系表
        LambdaQueryWrapper<CollectContentConnect> collectContentConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectContentConnectLambdaQueryWrapper.eq(CollectContentConnect::getType, CollectEnum.COLLECT_RESOURCE.getCode())
                .eq(CollectContentConnect::getAssociateId, resourceId);
        collectContentConnectMapper.delete(collectContentConnectLambdaQueryWrapper);

        // 删除elasticsearch资源
        resourceToSearchFeign.deleteResourceIndex(resourceId);
        return R.ok();
    }
}
