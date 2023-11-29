package com.monkey.monkeyquestion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyquestion.mapper.QuestionLabelMapper;
import com.monkey.monkeyquestion.mapper.QuestionMapper;
import com.monkey.monkeyquestion.pojo.Question;
import com.monkey.monkeyquestion.pojo.QuestionLabel;
import com.monkey.monkeyquestion.pojo.QuestionReply;
import com.monkey.monkeyquestion.pojo.vo.QuestionPublishVo;
import com.monkey.monkeyquestion.pojo.vo.QuestionVo;
import com.monkey.monkeyquestion.service.QuestionEditService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: wusihao
 * @date: 2023/11/28 18:11
 * @version: 1.0
 * @description:
 */
@Service
public class QuestionEditServiceImpl implements QuestionEditService {
    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionLabelMapper questionLabelMapper;
    @Resource
    private LabelMapper labelMapper;

    /**
     * 通过问答id查询问答信息
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/28 18:13
     */
    @Override
    public R queryQuestionById(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        LambdaQueryWrapper<QuestionLabel> questionLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        questionLabelLambdaQueryWrapper.eq(QuestionLabel::getQuestionId, questionId);
        questionLabelLambdaQueryWrapper.select(QuestionLabel::getLabelId);
        List<Object> questionLabelIdList = questionLabelMapper.selectObjs(questionLabelLambdaQueryWrapper);
        LambdaQueryWrapper<Label> labelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        labelLambdaQueryWrapper.in(Label::getId, questionLabelIdList);
        List<Label> labels = labelMapper.selectList(labelLambdaQueryWrapper);
        QuestionPublishVo questionPublishVo = new QuestionPublishVo();
        questionPublishVo.setTitle(question.getTitle());
        questionPublishVo.setProfile(question.getProfile());
        questionPublishVo.setLabelId(labels
                .stream()
                .map(m -> m.getId())
                .collect(Collectors.toList()));
        questionPublishVo.setLabelList(labels);
        questionPublishVo.setId(questionId);
        return R.ok(questionPublishVo);
    }

    /**
     * 更新问答信息
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/29 8:32
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R questionUpdate(QuestionPublishVo questionPublishVo) {
        Long questionId = questionPublishVo.getId();
        LambdaUpdateWrapper<Question> questionLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        questionLambdaUpdateWrapper.eq(Question::getId, questionId)
                                    .set(Question::getTitle, questionPublishVo.getTitle())
                                    .set(Question::getProfile, questionPublishVo.getProfile())
                                    .set(Question::getUpdateUser, JwtUtil.getUserId())
                                    .set(Question::getUpdateTime, new Date());
        questionMapper.update(null, questionLambdaUpdateWrapper);

        // 删除旧标签，添加新标签
        LambdaQueryWrapper<QuestionLabel> questionLabelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        questionLabelLambdaQueryWrapper.eq(QuestionLabel::getQuestionId, questionId);
        questionLabelMapper.delete(questionLabelLambdaQueryWrapper);

        List<Long> labelId = questionPublishVo.getLabelId();
        labelId.forEach(id -> {
            QuestionLabel questionLabel = new QuestionLabel();
            questionLabel.setQuestionId(questionId);
            questionLabel.setLabelId(id);
            questionLabelMapper.insert(questionLabel);
        });
        return R.ok();
    }
}
