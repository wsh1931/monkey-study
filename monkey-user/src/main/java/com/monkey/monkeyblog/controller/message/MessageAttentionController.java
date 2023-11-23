package com.monkey.monkeyblog.controller.message;

import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.message.MessageAttentionService;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/10/30 8:12
 * @version: 1.0
 * @description:
 */
@Api(tags = "消息关注接口")
@RestController
@RequestMapping("/monkey-user/message/attention")
public class MessageAttentionController {
    @Resource
    private MessageAttentionService messageAttentionService;

    @ApiOperation("查询关注消息集合")
    @GetMapping("/queryAttentionMessage")
    public R queryAttentionMessage(@RequestParam("currentPage") @ApiParam("当前页") Long currentPage,
                                 @RequestParam("pageSize") @ApiParam("每页数据量") Integer pageSize) {
        String userId = JwtUtil.getUserId();
        if (userId == null || "".equals(userId)) {
            return R.ok();
        }
        return messageAttentionService.queryAttentionMessage(Long.parseLong(userId), currentPage, pageSize);
    }

    @ApiOperation("删除关注消息")
    @DeleteMapping("/deleteAttentionMessage")
    public R deleteAttentionMessage(@RequestParam("messageId") @ApiParam("消息id") Long messageId) {
        return messageAttentionService.deleteAttentionMessage(messageId);
    }

    @ApiOperation("将所有关注消息置为已读")
    @PutMapping("/updateAllAttentionAlready")
    public R updateAllAttentionAlready() {
        String userId = JwtUtil.getUserId();
        return messageAttentionService.updateAllAttentionAlready(userId);
    }

    @ApiOperation("删除所有关注已读消息")
    @DeleteMapping("/deleteAllAttentionMessageOfAlreadyRead")
    public R deleteAllAttentionMessageOfAlreadyRead() {
        String userId = JwtUtil.getUserId();
        return messageAttentionService.deleteAllAttentionMessageOfAlreadyRead(userId);
    }
}
