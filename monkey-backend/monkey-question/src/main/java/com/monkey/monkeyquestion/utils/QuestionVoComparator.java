package com.monkey.monkeyquestion.utils;

import com.monkey.monkeyquestion.pojo.vo.QuestionVo;

import java.util.Comparator;

public class QuestionVoComparator implements Comparator<QuestionVo> {
    @Override
    public int compare(QuestionVo u1, QuestionVo u2) {
        if (u1.getVisit() != u2.getVisit()) {
            return Long.compare(u2.getVisit(), u1.getVisit()); // 成绩降序
        } else if (u1.getReplyCount() != u2.getReplyCount()) {
            return Long.compare(u2.getReplyCount(), u1.getReplyCount()); // 年龄降序
        } else {
            return Long.compare(u2.getConcernCount(), u1.getConcernCount()); // 学号降序
        }
    }
}