package com.monkey.monkeyarticle.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.pojo.vo.LabelVo;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyarticle.rabbitmq.EventConstant;
import com.monkey.monkeyarticle.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyarticle.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyarticle.service.PublishService;
import com.monkey.monkeyUtils.pojo.User;
import com.monkey.monkeyUtils.springsecurity.UserDetailsImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class PublishServiceImpl implements PublishService {
    @Resource
    private LabelMapper labelMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;
    // 发布文章
    @Override
    public ResultVO publishArticle(String content, String profile, String photo, String title, String labelId) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl userDetails = (UserDetailsImpl) usernamePasswordAuthenticationToken.getPrincipal();
        User user = userDetails.getUser();
        Long userId = user.getId();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.publishArticle);
        jsonObject.put("content", content);
        jsonObject.put("profile", profile);
        jsonObject.put("photo", photo);
        jsonObject.put("title", title);
        jsonObject.put("labelId", labelId);
        jsonObject.put("userId", userId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.articleInsertDirectExchange,
                RabbitmqRoutingName.articleInsertRouting, message);
        return new ResultVO(ResultStatus.OK, null, null);
    }

    /**
     * 得到一级标签列表
     *
     * @return {@link ResultVO}
     * @author wusihao
     * @date 2023/7/11 16:26
     */
    @Override
    public ResultVO getOneLevelLabelList() {
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("level", 1);
        List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
        return new ResultVO(ResultStatus.OK, null, labelList);
    }

    /**
     * 通过一级标签id查询二级标签列表
     *
     * @param labelOneId 一级标签id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/11 16:49
     */
    @Override
    public ResultVO getTwoLabelListByOneLabelId(Long labelOneId) {
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("parent_id", labelOneId);
        List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
        List<LabelVo> labelVoList = new ArrayList<>();
        for (Label label : labelList) {
            LabelVo labelVo = new LabelVo();
            BeanUtils.copyProperties(label, labelVo);
            labelVo.setSelected(false);
            labelVoList.add(labelVo);
        }
        return new ResultVO(ResultStatus.OK, "", labelVoList);
    }

    /**
     * 通过标签名模糊查找一级标签
     *
     * @param name 一级标签名
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/1 20:56
     */
    @Override
    public R likeSearchOneLabel(String name) {
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_ONE.getCode());
        labelQueryWrapper.like("label_name", name);
        return R.ok(labelMapper.selectList(labelQueryWrapper));
    }
}
