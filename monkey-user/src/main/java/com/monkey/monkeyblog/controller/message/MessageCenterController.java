package com.monkey.monkeyblog.controller.message;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.message.MessageCenterService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/27 11:03
 * @version: 1.0
 * @description:
 */
@Api(tags = "消息中心接口")
@RestController
@RequestMapping("/monkey-user/message/center")
public class MessageCenterController {
    @Resource
    private MessageCenterService messageCenterService;

    @ApiOperation("查询未查看评论回复消息数")
    @GetMapping("/queryNoCheckCommentCount")
    public R queryNoCheckCommentCount() {
        String userId = JwtUtil.getUserId();
        return messageCenterService.queryNoCheckCommentCount(userId);
    }

    @ApiOperation("查询未查看消息点赞数")
    @GetMapping("/queryNoCheckLikeCount")
    public R queryNoCheckLikeCount() {
        String userId = JwtUtil.getUserId();
        return messageCenterService.queryNoCheckLikeCount(userId);
    }

    @ApiOperation("查询未查看消息收藏数")
    @GetMapping("/queryNoCheckCollectCount")
    public R queryNoCheckCollectCount() {
        String userId = JwtUtil.getUserId();
        return messageCenterService.queryNoCheckCollectCount(userId);
    }
}
