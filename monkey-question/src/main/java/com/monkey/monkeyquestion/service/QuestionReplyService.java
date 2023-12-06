package com.monkey.monkeyquestion.service;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;

public interface QuestionReplyService {
    // 通过问答id得到作者Vo信息
    ResultVO getAuthorVoInfoByQuestionId(long questionId, String fansId);


    // 通过问答id得到问答信息
    ResultVO getQuestionInfoByQuestionId(long questionId, String userId);

    // 通过问答id得到问答标签名
    ResultVO getQuestionLabelNameByQuestionId(long questionId);

    // 通过问答id得到问答回复列表
    ResultVO getQuestionReplyListByQuestionId(long questionId, String fansId, Long currentPage, Long pageSize);

    // 用户问答点赞实现
    ResultVO userLikeQuestion(long questionId, long userId, Long recipientId);

    // 用户问答取消点赞实现
    ResultVO userCancelLikeQuestion(long questionId, long userId);

    // 通过问答回复id得到文章评论信息
    ResultVO getQuestionCommentByQuestionReplyId(long questionReplyId, Long currentPage, Long pageSize);

    // // 发表问答回复
    R publishReply(long questionId, long userId, String replyContent);

    // 判断问答是否存在
    R judgeQuestionIsExist(Long questionId);
}
