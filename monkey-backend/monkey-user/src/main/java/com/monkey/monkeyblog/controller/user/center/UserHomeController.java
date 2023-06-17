package com.monkey.monkeyblog.controller.user.center;

import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyblog.service.user.center.UserHomeService;
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
        return userHomeService.getUserInformationByUserId(data);
    }

    // 将访问者信息加入用户游览信息列表
    @PostMapping("/recentlyView")
    public ResultVO recentlyView(@RequestParam Map<String, String> data) {
        return userHomeService.recentlyView(data);
    }

    // 通过用户id得到最近来访用户信息
    @GetMapping("/getRecentlyUserInfoByUserId")
    public ResultVO getRecentlyUserInfoByUserId(@RequestParam Map<String, String> data) {
        return userHomeService.getRecentlyUserInfoByUserId(data);
    }

    // 通过用户id得到用户所发表的所有文章分类数
    @GetMapping("/getUserArticleClassficationCountByuserId")
    public ResultVO getUserArticleClassficationCountByuserId(@RequestParam Map<String, String> data) {
        return userHomeService.getUserArticleClassficationCountByuserId(data);
    }

    // 通过用户id得到文章列表
    @GetMapping("/getArticleListByUserId")
    public ResultVO getArticleListByUserId(@RequestParam Map<String, String> data) {
        return userHomeService.getArticleListByUserId(data);
    }

    // 通过用户id得到用户粉丝列表
    @GetMapping("/getFansListByUserId")
    public ResultVO getFansListByUserId(@RequestParam Map<String, String> data) {
        return userHomeService.getFansListByUserId(data);
    }

    // 通过用户id得到关注列表
    @GetMapping("/getConcernListByUserId")
    public ResultVO getConcernListByUserId(@RequestParam Map<String, String> data) {
        return userHomeService.getConcernListByUserId(data);
    }

    // 通过用户id得到用户收藏文章列表
    @GetMapping("/getUserCollectArticleListByUserId")
    public ResultVO getUserCollectArticleListByUserId(@RequestParam Map<String, String> data) {
        return userHomeService.getUserCollectArticleListByUserId(data);
    }

    // 提交编辑资料之后更新用户信息
    @PutMapping("/updateInformation")
    public ResultVO updateInformation(@RequestParam Map<String, String> data) {
        return userHomeService.updateInformation(data);
    }
}
