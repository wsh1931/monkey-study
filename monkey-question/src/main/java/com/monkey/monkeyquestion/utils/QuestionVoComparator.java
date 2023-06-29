package com.monkey.monkeyquestion.utils;

import com.monkey.monkeyquestion.pojo.vo.QuestionVo;

import java.util.Comparator;

public class QuestionVoComparator implements Comparator<QuestionVo> {
    @Override
    public int compare(QuestionVo u1, QuestionVo u2) {
        if (u1.getUserLikeCount() != u2.getUserLikeCount()) {
          return Long.compare(u2.getUserLikeCount(), u1.getUserCollectCount());
        } else if (u1.getVisit() != u2.getVisit()) {
            return Long.compare(u2.getVisit(), u1.getVisit()); // 访问量
        } else if (u1.getReplyCount() != u2.getReplyCount()) {
            return Long.compare(u2.getReplyCount(), u1.getReplyCount()); // 回复量
        } else {
            return Long.compare(u2.getUserCollectCount(), u1.getUserCollectCount()); // 收藏量
        }
    }
}