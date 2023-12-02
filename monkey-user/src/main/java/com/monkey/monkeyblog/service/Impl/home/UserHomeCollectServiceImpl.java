package com.monkey.monkeyblog.service.Impl.home;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CollectEnum;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.mapper.CollectContentMapper;
import com.monkey.monkeyUtils.pojo.CollectContent;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.service.home.UserHomeCollectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/12/1 12:11
 * @version: 1.0
 * @description:
 */
@Service
public class UserHomeCollectServiceImpl implements UserHomeCollectService {
    @Resource
    private CollectContentMapper collectContentMapper;
    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;

    /**
     * 查询用户收藏目录信息
     *
     * @param userId 用户id
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/1 15:36
     */
    @Override
    public R queryUserCollectContent(String userId, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<CollectContent> collectContentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectContentLambdaQueryWrapper.eq(CollectContent::getUserId, userId);
        collectContentLambdaQueryWrapper.eq(CollectContent::getIsPrivate, CommonEnum.COLLECT_IS_PUBLIC.getCode());
        collectContentLambdaQueryWrapper.orderByDesc(CollectContent::getCreateTime);
        Page page = new Page(currentPage, pageSize);
        Page selectPage = collectContentMapper.selectPage(page, collectContentLambdaQueryWrapper);
        return R.ok(selectPage);
    }

    /**
     * 查询收藏详细内容
     *
     * @param collectContentId 收藏目录id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/1 16:18
     */
    @Override
    public R queryCollectContentDetail(String collectContentId) {
        LambdaQueryWrapper<CollectContentConnect> collectContentConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectContentConnectLambdaQueryWrapper.eq(CollectContentConnect::getCollectContentId, collectContentId);
        collectContentConnectLambdaQueryWrapper.orderByDesc(CollectContentConnect::getCreateTime);
        List<CollectContentConnect> collectContentConnectList = collectContentConnectMapper.selectList(collectContentConnectLambdaQueryWrapper);
        collectContentConnectList.forEach(collectContentConnect -> {
            Integer type = collectContentConnect.getType();
            CollectEnum collectEnum = CollectEnum.getCollectEnum(type);
            collectContentConnect.setTypename(collectEnum.getMsg());
        });
        return R.ok(collectContentConnectList);
    }
}
