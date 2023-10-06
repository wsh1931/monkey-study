package com.monkey.monkeycommunity.service.impl.manage;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycommunity.constant.TipConstant;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.mapper.CommunityChannelMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.pojo.CommunityChannel;
import com.monkey.monkeycommunity.rabbitmq.EventConstant;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeycommunity.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeycommunity.service.manage.ChannelManageService;
import netscape.javascript.JSObject;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/10/5 16:03
 * @version: 1.0
 * @description:
 */
@Service
public class ChannelManageServiceImpl implements ChannelManageService {
    @Resource
    private CommunityChannelMapper communityChannelMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    /**
     * 查询社区频道管理列表
     *
     * @param communityId 社区id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @param channelName 频道名称
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/5 16:06
     */
    @Override
    public R queryChannelManageList(Long communityId, Long currentPage, Integer pageSize, String channelName) {
        QueryWrapper<CommunityChannel> communityChannelQueryWrapper = new QueryWrapper<>();
        communityChannelQueryWrapper.eq("community_id", communityId);
        communityChannelQueryWrapper.orderByAsc("sort");
        communityChannelQueryWrapper.like(channelName != null && !"".equals(channelName), "channel_name", channelName);
        Page page = new Page(currentPage, pageSize);
        Page selectPage = communityChannelMapper.selectPage(page, communityChannelQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 更新是否支持前端展示
     *
     * @param channelId 频道id
     * @param supportShow 是否支持前端展示字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/5 17:46
     */
    @Override
    public R updateSupportShow(Long channelId, Integer supportShow) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.updateSupportShow);
        data.put("channelId", channelId);
        data.put("supportShow", supportShow);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok(null, TipConstant.operateSuccess);
    }

    /**
     * 更新是否支持用户发表文章
     *
     * @param channelId 频道id
     * @param supportUserPublish 是否支持用户发表字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/5 17:46
     */
    @Override
    public R updateSupportUserPublish(Long channelId, Integer supportUserPublish) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.updateSupportUserPublish);
        data.put("channelId", channelId);
        data.put("supportUserPublish", supportUserPublish);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok(null, TipConstant.operateSuccess);
    }

    /**
     * 更新是否支持管理员修改
     *
     * @param channelId 频道id
     * @param supportManageModify 是否支持管理员修改字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/5 17:47
     */
    @Override
    public R updateSupportManageModify(Long channelId, Integer supportManageModify) {
        JSONObject data = new JSONObject();
        data.put("event", EventConstant.updateSupportManageModify);
        data.put("channelId", channelId);
        data.put("supportManageModify", supportManageModify);
        Message message = new Message(data.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.communityUpdateDirectExchange,
                RabbitmqRoutingName.communityUpdateRouting, message);
        return R.ok(null, TipConstant.operateSuccess);
    }

    /**
     * 提交频道编辑
     *
     * @param communityChannel 频道实体类
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 9:36
     */
    @Override
    public R submitChannelEdit(CommunityChannel communityChannel) {
        Long channelId = communityChannel.getId();
        String channelName = communityChannel.getChannelName();
        Long sort = communityChannel.getSort();
        UpdateWrapper<CommunityChannel> communityChannelUpdateWrapper = new UpdateWrapper<>();
        communityChannelUpdateWrapper.eq("id", channelId);
        communityChannelUpdateWrapper.set("channel_name", channelName);
        communityChannelUpdateWrapper.set("sort", sort);
        communityChannelUpdateWrapper.set("update_time", new Date());
        communityChannelMapper.update(null, communityChannelUpdateWrapper);
        return R.ok();
    }

    /**
     * 删除频道
     *
     * @param channelId 频道id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 9:52
     */
    @Override
    public R deleteChannel(Long channelId) {
        // 判断该频道下是否存在社区文章
        QueryWrapper<CommunityArticle> communityArticleQueryWrapper = new QueryWrapper<>();
        communityArticleQueryWrapper.eq("channel_id", channelId);
        Long selectCount = communityArticleMapper.selectCount(communityArticleQueryWrapper);
        if (selectCount > 0) {
            return R.error(TipConstant.channelExistArticle);
        }
        int deleteById = communityChannelMapper.deleteById(channelId);
        if (deleteById > 0) {
            return R.ok(null, TipConstant.operateSuccess);
        }
        return R.error(TipConstant.noExistAgain);
    }

    /**
     * 新增频道
     *
     * @param communityId 社区id
     * @param channelName 频道名称
     * @param sort 排序字段
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/6 10:32
     */
    @Override
    public R addChannel(Long communityId, String channelName, Long sort) {
        CommunityChannel channel = new CommunityChannel();
        channel.setSort(sort);
        channel.setCommunityId(communityId);
        channel.setChannelName(channelName);
        Date date = new Date();
        channel.setCreateTime(date);
        channel.setUpdateTime(date);
        communityChannelMapper.insert(channel);
        return R.ok();
    }
}
