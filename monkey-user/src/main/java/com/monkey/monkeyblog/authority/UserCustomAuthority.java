package com.monkey.monkeyblog.authority;

import com.monkey.monkeyUtils.mapper.CollectContentMapper;
import com.monkey.monkeyUtils.pojo.CollectContent;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: wusihao
 * @date: 2023/12/6 8:47
 * @version: 1.0
 * @description:
 */
@Component
public class UserCustomAuthority {
    @Resource
    private CollectContentMapper collectContentMapper;
    /**
     * 判断当前操作的文件夹是否是用户本人
     *
     * @param collectContentId 收藏目录id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/6 8:47
     */
    public boolean judgeIsAuthorByCollectContentId(Long collectContentId) {
        CollectContent collectContent = collectContentMapper.selectById(collectContentId);
        return collectContent.getUserId().equals(Long.parseLong(JwtUtil.getUserId()));
    }
}
