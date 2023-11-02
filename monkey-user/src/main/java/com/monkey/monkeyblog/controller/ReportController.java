package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.ReportService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/11/1 16:23
 * @version: 1.0
 * @description:
 */
@Api(tags = "举报功能接口")
@RestController
@RequestMapping("/monkey-user/report")
public class ReportController {
    @Resource
    private ReportService reportService;

    @ApiOperation("查询一级举报类型集合")
    @GetMapping("/queryOneReportType")
    public R queryOneReportType() {
        return reportService.queryOneReportType();
    }

    @ApiOperation("查询二级举报类型集合")
    @GetMapping("/queryTwoReportType")
    public R queryTwoReportType(@RequestParam("oneReportTypeId") @ApiParam("一级举报类型id") Long oneReportTypeId) {
        return reportService.queryTwoReportType(oneReportTypeId);
    }

    @ApiOperation("提交举报内容类型")
    @PostMapping("/submitReportContentType")
    public R submitReportContentType(@RequestParam("oneReportTypeId") @ApiParam("一级举报类型id") Long oneReportTypeId,
                                     @RequestParam("twoReportTypeId") @ApiParam("二级举报类型id") String twoReportTypeId,
                                     @RequestParam("reportDetail") @ApiParam("举报详情") String reportDetail,
                                     @RequestParam("reportContentType") @ApiParam("举报内容类型") Integer reportContentType,
                                     @RequestParam("reportContentAssociationId") @ApiParam("举报内容关联内容jd") Long reportContentAssociationId) {
        long userId = Long.parseLong(JwtUtil.getUserId());
        return reportService.submitReportContentType(userId, oneReportTypeId, twoReportTypeId, reportDetail, reportContentType, reportContentAssociationId);
    }
}
