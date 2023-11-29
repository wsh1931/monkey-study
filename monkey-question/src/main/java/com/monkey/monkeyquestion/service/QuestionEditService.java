package com.monkey.monkeyquestion.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyquestion.pojo.vo.QuestionPublishVo;

public interface QuestionEditService {
    // 通过问答id查询问答信息
    R queryQuestionById(Long questionId);

    // 更新问答信息
    R questionUpdate(QuestionPublishVo questionPublishVo);
}
