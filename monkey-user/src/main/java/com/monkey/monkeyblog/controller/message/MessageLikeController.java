package com.monkey.monkeyblog.controller.message;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.message.MessageLikeService;
import com.monkey.spring_security.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/28 14:13
 * @version: 1.0
 * @description:
 */
@Api(tags = "消息点赞接口")
@RestController
@RequestMapping("/monkey-user/message/like")
public class MessageLikeController {
    @Resource
    private MessageLikeService messageLikeService;

    @ApiOperation("查询点赞消息集合")
    @GetMapping("/queryLikeMessage")
    public R queryLikeMessage(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                      @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        String userId = JwtUtil.getUserId();
        if (userId == null || "".equals(userId)) {
            return R.ok();
        }
        return messageLikeService.queryLikeMessage(Long.parseLong(userId), currentPage, pageSize);
    }

    @ApiOperation("删除点赞消息")
    @DeleteMapping("/deleteLikeMessage")
    public R deleteLikeMessage(@RequestParam("messageId") @ApiParam("消息id") Long messageId) {
        return messageLikeService.deleteLikeMessage(messageId);
    }

    @ApiOperation("将所有点赞消息置为已读")
    @PutMapping("/updateAllLikeAlready")
    public R updateAllLikeAlready() {
        String userId = JwtUtil.getUserId();
        return messageLikeService.updateAllLikeAlready(userId);
    }

    @ApiOperation("删除所有点赞已读消息")
    @DeleteMapping("/deleteAllLikeMessageOfAlreadyRead")
    public R deleteAllLikeMessageOfAlreadyRead() {
        String userId = JwtUtil.getUserId();
        return messageLikeService.deleteAllLikeMessageOfAlreadyRead(userId);
    }
}
