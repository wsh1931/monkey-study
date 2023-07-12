package com.monkey.monkeyblog.controller;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.service.user.UserHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user/center/home")
public class UserHomeController {

    @Autowired
    private UserHomeService userHomeService;

    // 通过用户id得到用户vo信息
    @GetMapping("/getUserInformationByUserId")
    public ResultVO getUserInformationByUserId(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        String nowUserId1 = data.get("nowUserId");
        return userHomeService.getUserInformationByUserId(userId, nowUserId1);
    }

    // 将访问者信息加入用户游览信息列表
    @PostMapping("/recentlyView")
    public ResultVO recentlyView(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long reviewId = Long.parseLong(data.get("reviewId"));
        return userHomeService.recentlyView(userId, reviewId);
    }

    // 通过用户id得到最近来访用户信息
    @GetMapping("/getRecentlyUserInfoByUserId")
    public ResultVO getRecentlyUserInfoByUserId(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        return userHomeService.getRecentlyUserInfoByUserId(userId);
    }

    // 通过用户id得到用户所发表的所有文章分类数
    @GetMapping("/getUserArticleClassficationCountByuserId")
    public ResultVO getUserArticleClassficationCountByuserId(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        return userHomeService.getUserArticleClassficationCountByuserId(userId);
    }

    // 通过用户id得到文章列表
    @GetMapping("/getArticleListByUserId")
    public ResultVO getArticleListByUserId(@RequestParam Map<String, String> data) {
        Long currentPage = Long.parseLong(data.get("currentPage"));
        Long pageSize = Long.parseLong(data.get("pageSize"));
        Long labelId = Long.parseLong(data.get("labelId"));
        String userId = data.get("userId");
        return userHomeService.getArticleListByUserId(currentPage, pageSize, labelId, userId);
    }

    // 通过用户id得到用户粉丝列表
    @GetMapping("/getFansListByUserId")
    public ResultVO getFansListByUserId(@RequestParam Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        long userId = Long.parseLong(data.get("userId"));
        String nowUserId = data.get("nowUserId");
        return userHomeService.getFansListByUserId(currentPage, pageSize, userId, nowUserId);
    }

    // 通过用户id得到关注列表
    @GetMapping("/getConcernListByUserId")
    public ResultVO getConcernListByUserId(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        String nowUserId = data.get("nowUserId");
        return userHomeService.getConcernListByUserId(currentPage, pageSize, userId, nowUserId);
    }

    // 通过用户id得到用户收藏文章列表
    @GetMapping("/getUserCollectArticleListByUserId")
    public ResultVO getUserCollectArticleListByUserId(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        String nowUserId = data.get("nowUserId");
        return userHomeService.getUserCollectArticleListByUserId(currentPage, pageSize, userId, nowUserId);
    }

    // 提交编辑资料之后更新用户信息
    @PutMapping("/updateInformation")
    public ResultVO updateInformation(@RequestParam Map<String, String> data) {
        String userInformation1 = data.get("userInformation");
        return userHomeService.updateInformation(userInformation1);
    }

    // 通过用户id得到文章提问列表
    @GetMapping("/getQuestionListByUserId")
    public ResultVO getQuestionListByUserId(@RequestParam Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        return userHomeService.getQuestionListByUserId(userId, currentPage, pageSize);
    }
}
