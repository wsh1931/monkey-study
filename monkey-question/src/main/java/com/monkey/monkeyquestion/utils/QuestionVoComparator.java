package com.monkey.monkeyquestion.utils;

import com.monkey.monkeyquestion.pojo.vo.QuestionVo;

import java.util.Comparator;

public class QuestionVoComparator implements Comparator<QuestionVo> {
    @Override
    public int compare(QuestionVo u1, QuestionVo u2) {
        if (!u1.getUserLikeCount().equals(u2.getUserLikeCount())) {
          return Long.compare(u2.getUserLikeCount(), u1.getUserCollectCount());
        } else if (!u1.getViewCount().equals(u2.getViewCount())) {
            // 访问量
            return Long.compare(u2.getViewCount(), u1.getViewCount());
        } else if (!u1.getReplyCount().equals(u2.getReplyCount())) {
            // 回复量
            return Long.compare(u2.getReplyCount(), u1.getReplyCount());
        } else {
            // 收藏量
            return Long.compare(u2.getUserCollectCount(), u1.getUserCollectCount());
        }
    }
}