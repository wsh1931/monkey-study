package com.monkey.monkeyquestion.authority;

import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyquestion.mapper.QuestionMapper;
import com.monkey.monkeyquestion.pojo.Question;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/29 8:28
 * @version: 1.0
 * @description:
 */
@Component
public class QuestionCustomAuthority {
    @Resource
    private QuestionMapper questionMapper;
    /**
     * 判断此问答发布者是否是作者
     *
     * @param questionId 问答id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/29 8:28
     */
    public boolean judgeIsAuthor(String questionId) {
        Question question = questionMapper.selectById(questionId);
        return JwtUtil.getUserId().equals(String.valueOf(question.getUserId()));
    }

}
