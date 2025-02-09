package com.monkey.monkeycommunity;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.monkey.monkeyUtils.springsecurity.UserDetailsImpl;
import com.monkey.monkeyUtils.util.DateUtils;
import com.monkey.monkeycommunity.mapper.CommunityArticleMapper;
import com.monkey.monkeycommunity.mapper.CommunityArticleTaskReplyMapper;
import com.monkey.monkeycommunity.pojo.CommunityArticle;
import com.monkey.monkeycommunity.pojo.CommunityArticleTaskReply;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.PackageUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/9/21 17:33
 * @version: 1.0
 * @description:
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EasyExcelTest {
    @Resource
    private CommunityArticleTaskReplyMapper communityArticleTaskReplyMapper;
    @Resource
    private CommunityArticleMapper communityArticleMapper;
    @Test
    public void dateTest() {
        CommunityArticle communityArticle = communityArticleMapper.selectById(14);
        LambdaQueryWrapper<CommunityArticle> communityArticleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        communityArticleLambdaQueryWrapper.eq(CommunityArticle::getId, 14);
        communityArticleLambdaQueryWrapper.eq(CommunityArticle::getCreateTime, DateUtils.format(communityArticle.getCreateTime()));
        Long selectCount = communityArticleMapper.selectCount(communityArticleLambdaQueryWrapper);
        System.out.println(selectCount);
    }
    @Test
    public void getToken() {

    }

    @Test
    public void getUserAuthorityInfo() {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) usernamePasswordAuthenticationToken.getPrincipal();
    }
    @Test
    public void repeatedWrite() {
        // 方法1: 如果写到同一个sheet
        String fileName = "D:\\easyExcelTest\\" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写
        try (ExcelWriter excelWriter = EasyExcel.write(fileName, CommunityArticleTaskReply.class).build()) {
            // 这里注意 如果同一个sheet只要创建一次
            WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
            // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来
            for (int i = 0; i < 5; i++) {
                // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                List<CommunityArticleTaskReply> data = communityArticleTaskReplyMapper.selectList(null);
                excelWriter.write(data, writeSheet);
            }
        }
    }


}
