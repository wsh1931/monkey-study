package com.monkey.monkeyblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.mapper.CollectContentMapper;
import com.monkey.monkeyUtils.pojo.CollectContent;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.UserCollectService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/7/31 17:08
 * @version: 1.0
 * @description:
 */
@Service
public class UserCollectServiceImpl implements UserCollectService {
    @Autowired
    private CollectContentMapper collectContentMapper;
    @Autowired
    private CollectContentConnectMapper collectContentConnectMapper;

    /**
     * 通过用户id得到用户收藏目录
     *
     * @param userId      用户id
     * @param associateId
     * @param collectType
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 17:14
     */
    @Override
    public R getCollectContentListByUserId(long userId, long associateId, long collectType) {
        QueryWrapper<CollectContent> collectContentQueryWrapper = new QueryWrapper<>();
        collectContentQueryWrapper.eq("user_id", userId);
        List<CollectContent> collectContentList = collectContentMapper.selectList(collectContentQueryWrapper);
        // 判断该登录用户是否收藏此目录
        for (int i = 0; i < collectContentList.size(); i ++ ) {
            CollectContent collectContent = collectContentList.get(i);
            Long collectContentId = collectContent.getId();
            QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
            collectContentConnectQueryWrapper.eq("user_id", userId);
            collectContentConnectQueryWrapper.eq("associate_id", associateId);
            collectContentConnectQueryWrapper.eq("type", collectType);
            collectContentConnectQueryWrapper.eq("collect_content_id", collectContentId);
            Long selectCount = collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper);
            if (selectCount > 0) {
                collectContent.setIsCollect(CommonEnum.COLLECT.getCode());
            } else {
                collectContent.setIsCollect(CommonEnum.UNCOLLECT.getCode());
            }
            collectContentList.set(i, collectContent);
        }
        return R.ok(collectContentList);
    }

    /**
     * 创建收藏夹
     *
     * @param content 收藏目录
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/1 17:32
     */
    @Override
    public R createContent(CollectContent content) {
        content.setCreateTime(new Date());
        int insert = collectContentMapper.insert(content);
        if (insert > 0) {
            return R.ok("创建文件夹成功");
        } else {
            return R.error("发送位置错误，创建文件夹失败");
        }
    }

    /**
     * 收藏功能实现
     *
     * @param collectContentId 收藏目录id
     * @param associateId 关联收藏id
     * @param collectType 收藏类型
     * @param collectTitle 收藏标题
     * @param userId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/5 11:21
     */
    @Override
    public R collectContent(long collectContentId, long associateId, int collectType, String collectTitle, long userId) {
        QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
        collectContentConnectQueryWrapper.eq("collect_content_id", collectContentId);
        collectContentConnectQueryWrapper.eq("user_id", userId);
        collectContentConnectQueryWrapper.eq("associate_id", associateId);
        collectContentConnectQueryWrapper.eq("type", collectType);
        CollectContentConnect collectContentConnect = collectContentConnectMapper.selectOne(collectContentConnectQueryWrapper);
        if (collectContentConnect == null) {
            // 添加至收藏目录关系表
            CollectContentConnect contentConnect = new CollectContentConnect();
            contentConnect.setUserId(userId);
            contentConnect.setCollectContentId(collectContentId);
            contentConnect.setType(collectType);
            contentConnect.setTitle(collectTitle);
            contentConnect.setAssociateId(associateId);
            contentConnect.setCreateTime(new Date());
            int insert = collectContentConnectMapper.insert(contentConnect);
            if (insert > 0) {
                // 收藏目录表收藏数 + 1;
                CollectContent collectContent = collectContentMapper.selectById(collectContentId);
                collectContent.setCollectCount(collectContent.getCollectCount() + 1);
                collectContentMapper.updateById(collectContent);
                return R.ok("收藏成功");
            } else {
                return R.error("发生未知错误，收藏失败");
            }
            // 收藏目录表的收藏数 + 1

        } else {
            int deleteById = collectContentConnectMapper.deleteById(collectContentConnect);
            if (deleteById > 0) {
                // 收藏目录表收藏数 - 1;
                CollectContent collectContent = collectContentMapper.selectById(collectContentId);
                collectContent.setCollectCount(collectContent.getCollectCount() - 1);
                collectContentMapper.updateById(collectContent);
                return R.ok("取消收藏成功");
            } else {
                return R.error("发生未知错误，取消收藏失败");
            }
        }
    }
}
