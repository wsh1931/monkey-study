package com.monkey.monkeyblog.service;

import com.monkey.monkeyUtils.result.R;

public interface ReportService {
    // 查询一级举报类型集合
    R queryOneReportType();

    // 查询二级举报类型集合
    R queryTwoReportType(Long oneReportTypeId);

    // 提交举报内容类型
    R submitReportContentType(long userId, Long oneReportTypeId, String twoReportTypeId, String reportDetail, Integer reportContentType, Long reportContentAssociationId);

    // 提交举报评论类型
    R submitReportCommentType(long userId, Long oneReportTypeId, String twoReportTypeId, String reportDetail, Integer reportCommentType, Long reportCommentAssociationId);
}
