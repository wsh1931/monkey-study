package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.service.UserHomeService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(tags = "用户主页接口")
@RestController
@RequestMapping("/monkey-user/user/center/home")
public class UserHomeController {

    @Resource
    private UserHomeService userHomeService;

    @ApiOperation("通过用户id得到用户vo信息")
    @GetMapping("/getUserInformationByUserId")
    public ResultVO getUserInformationByUserId(@RequestParam("userId") @ApiParam("作者id") Long userId) {
        String nowUserId1 = JwtUtil.getUserId();
        return userHomeService.getUserInformationByUserId(userId, nowUserId1);
    }

    @ApiOperation("将访问者信息加入用户游览信息列表")
    @PostMapping("/recentlyView")
    public ResultVO recentlyView(@RequestParam("userId") @ApiParam("作者id") Long userId) {
        long reviewId = Long.parseLong(JwtUtil.getUserId());
        return userHomeService.recentlyView(userId, reviewId);
    }

    @ApiOperation("通过用户id得到最近来访用户信息")
    @GetMapping("/getRecentlyUserInfoByUserId")
    public ResultVO getRecentlyUserInfoByUserId(@RequestParam("userId") @ApiParam("作者id") Long userId) {
        return userHomeService.getRecentlyUserInfoByUserId(userId);
    }

    @ApiOperation("通过用户id得到用户所发表的所有文章分类数")
    @GetMapping("/getUserArticleClassficationCountByuserId")
    public ResultVO getUserArticleClassficationCountByuserId(@RequestParam("userId") @ApiParam("作者id") Long userId) {
        return userHomeService.getUserArticleClassficationCountByuserId(userId);
    }

    @ApiOperation("通过用户id得到文章列表")
    @GetMapping("/getArticleListByUserId")
    public R getArticleListByUserId(@RequestParam("labelId") @ApiParam("文章标签id") Long labelId,
                                    @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                    @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize,
                                    @RequestParam("userId") @ApiParam("作者id") String userId) {
        return userHomeService.getArticleListByUserId(currentPage, pageSize, labelId, userId);
    }

    @ApiOperation("通过用户id得到用户粉丝列表")
    @GetMapping("/getFansListByUserId")
    public ResultVO getFansListByUserId(@RequestParam("userId") @ApiParam("作者id") Long userId,
                                        @RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                        @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        String nowUserId = JwtUtil.getUserId();
        return userHomeService.getFansListByUserId(currentPage, pageSize, userId, nowUserId);
    }

    @ApiOperation("通过用户id得到关注列表")
    @GetMapping("/getConcernListByUserId")
    public ResultVO getConcernListByUserId(@RequestParam("userId") @ApiParam("作者id") Long userId,
                                           @RequestParam("currentPage") @ApiParam("当前页") Integer currentPage,
                                           @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        String nowUserId = JwtUtil.getUserId();
        return userHomeService.getConcernListByUserId(currentPage, pageSize, userId, nowUserId);
    }

    @ApiOperation("提交编辑资料之后更新用户信息")
    @PutMapping("/updateInformation")
    public ResultVO updateInformation(@RequestParam("userInformation") @ApiParam("用户信息字符串实体类") String userInformation) {
        return userHomeService.updateInformation(userInformation);
    }

    @ApiOperation("通过用户id得到文章提问列表")
    @GetMapping("/getQuestionListByUserId")
    public R getQuestionListByUserId(@RequestParam("userId") @ApiParam("作者id") Long userId,
                                     @RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                     @RequestParam("pageSize") @ApiParam("每页数据量") Long pageSize) {
        return userHomeService.getQuestionListByUserId(userId, currentPage, pageSize);
    }
}
