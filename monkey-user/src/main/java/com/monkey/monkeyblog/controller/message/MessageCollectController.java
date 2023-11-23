package com.monkey.monkeyblog.controller.message;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.message.MessageCollectService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/29 21:48
 * @version: 1.0
 * @description:
 */
@Api(tags = "消息收藏接口")
@RestController
@RequestMapping("/monkey-user/message/collect")
public class MessageCollectController {
    @Resource
    private MessageCollectService messageCollectService;

    @ApiOperation("查询收藏消息集合")
    @GetMapping("/queryCollectMessage")
    public R queryCollectMessage(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                              @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        String userId = JwtUtil.getUserId();
        if (userId == null || "".equals(userId)) {
            return R.ok();
        }
        return messageCollectService.queryCollectMessage(Long.parseLong(userId), currentPage, pageSize);
    }

    @ApiOperation("删除收藏消息")
    @DeleteMapping("/deleteCollectMessage")
    public R deleteCollectMessage(@RequestParam("messageId") @ApiParam("消息id") Long messageId) {
        return messageCollectService.deleteCollectMessage(messageId);
    }

    @ApiOperation("将所有收藏消息置为已读")
    @PutMapping("/updateAllCollectAlready")
    public R updateAllCollectAlready() {
        String userId = JwtUtil.getUserId();
        return messageCollectService.updateAllCollectAlready(userId);
    }

    @ApiOperation("删除所有收藏已读消息")
    @DeleteMapping("/deleteAllCollectMessageOfAlreadyRead")
    public R deleteAllCollectMessageOfAlreadyRead() {
        String userId = JwtUtil.getUserId();
        return messageCollectService.deleteAllCollectMessageOfAlreadyRead(userId);
    }
}
