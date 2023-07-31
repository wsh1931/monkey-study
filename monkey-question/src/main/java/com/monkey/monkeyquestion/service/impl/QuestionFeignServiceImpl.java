package com.monkey.monkeyquestion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.pojo.Collect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyquestion.mapper.QuestionMapper;
import com.monkey.monkeyquestion.mapper.QuestionReplyMapper;
import com.monkey.monkeyquestion.pojo.Question;
import com.monkey.monkeyquestion.pojo.QuestionLike;
import com.monkey.monkeyquestion.pojo.QuestionReply;
import com.monkey.monkeyquestion.pojo.vo.QuestionVo;
import com.monkey.monkeyquestion.service.QuestionFeignService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/7/31 10:09
 * @version: 1.0
 * @description:
 */
@Service
public class QuestionFeignServiceImpl implements QuestionFeignService {
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionReplyMapper questionReplyMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 通过用户id得到问答列表
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 10:13
     */
    @Override
    public R getQuestionListByQuestionId(Long userId) {
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("user_id", userId);
        List<Question> questionList = questionMapper.selectList(questionQueryWrapper);
        return R.ok(questionList);
    }

    /**
     * 通过用户id得到用户提问数
     *
     * @param userId 用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 10:38
     */
    @Override
    public R getUserQuestionCountByUserId(Long userId) {
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("user_id", userId);
        Long selectCount = questionMapper.selectCount(questionQueryWrapper);
        return R.ok(selectCount);
    }

    /**
     * 通过用户id得到文章分页提问列表
     *
     * @param userId 用户id
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return {@link null}
     * @author wusihao
     * @date 2023/7/31 11:39
     */
    @Override
    public R getQuestionListByUserId(Long userId, Long currentPage, Long pageSize) {
        Page page = new Page<>(currentPage, pageSize);
        QueryWrapper<Question> questionQueryWrapper = new QueryWrapper<>();
        questionQueryWrapper.eq("user_id", userId);
        questionQueryWrapper.orderByDesc("create_time");
        Page selectPage = questionMapper.selectPage(page, questionQueryWrapper);
        List<Question> questionList = selectPage.getRecords();
        List<QuestionVo> questionVoList = new ArrayList<>();
        for (Question question : questionList) {
            Long questionId = question.getId();
            QuestionVo questionVo = new QuestionVo();
            BeanUtils.copyProperties(question, questionVo);

            // 得到问答回复数
            QueryWrapper<QuestionReply> questionReplyQueryWrapper = new QueryWrapper<>();
            questionReplyQueryWrapper.eq("question_id", questionId);
            Long replyCount = questionReplyMapper.selectCount(questionReplyQueryWrapper);
            questionVo.setReplyCount(replyCount);

            // 得到提问收藏数
            questionVo.setUserCollectCount(question.getCollectCount());

            // 得到提问点赞数
            questionVo.setUserLikeCount(question.getLikeCount());

            // 通过用户id得到用户头像，姓名
            User user = userMapper.selectById(question.getUserId());
            questionVo.setUsername(user.getUsername());
            questionVo.setUserphoto(user.getPhoto());
            questionVoList.add(questionVo);
        }

        selectPage.setRecords(questionVoList);
        return R.ok(selectPage);
    }
}
