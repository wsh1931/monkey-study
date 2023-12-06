package com.monkey.monkeyblog.service.Impl.center;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.mapper.CollectContentMapper;
import com.monkey.monkeyUtils.pojo.CollectContent;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.springsecurity.JwtUtil;
import com.monkey.monkeyblog.service.center.UserCenterCollectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/12/5 15:07
 * @version: 1.0
 * @description:
 */
@Service
public class UserCenterCollectServiceImpl implements UserCenterCollectService {
    @Resource
    private CollectContentMapper collectContentMapper;
    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;
    /**
     * 查询收藏目录以及对应的收藏数
     *
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/5 15:15
     */
    @Override
    public R queryCollectContent(Long currentPage, Integer pageSize) {
        String userId = JwtUtil.getUserId();
        LambdaQueryWrapper<CollectContent> collectContentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectContentLambdaQueryWrapper.eq(CollectContent::getUserId, userId)
                .orderByDesc(CollectContent::getCreateTime);
        Page<CollectContent> collectContentPage = collectContentMapper
                .selectPage(new Page<>(currentPage, pageSize), collectContentLambdaQueryWrapper);
        return R.ok(collectContentPage);
    }

    /**
     * 查询收藏目录信息通过收藏目录id
     *
     * @param collectContentId 收藏目录id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/5 16:09
     */
    @Override
    public R queryCollectContentById(Long collectContentId) {
        return R.ok(collectContentMapper.selectById(collectContentId));
    }

    /**
     * 查询收藏目录关系列表
     *
     * @param collectContentId 收藏目录id
     * @param type 收藏类型
     * @param keyword 搜索关键字
     * @param currentPage 当前页
     * @param pageSize 每页数据量
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/5 16:29
     */
    @Override
    public R queryContentConnect(Long collectContentId, Integer type, String keyword, Long currentPage, Integer pageSize) {
        LambdaQueryWrapper<CollectContentConnect> collectContentConnectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectContentConnectLambdaQueryWrapper.eq(CollectContentConnect::getCollectContentId, collectContentId)
                .like(keyword != null && !"".equals(keyword), CollectContentConnect::getTitle, keyword)
                .eq(type != -1, CollectContentConnect::getType, type)
                .orderByDesc(CollectContentConnect::getCreateTime);

        Page<CollectContentConnect> collectContentConnectPage = collectContentConnectMapper.selectPage(new Page<>(currentPage, pageSize),
                collectContentConnectLambdaQueryWrapper);

        return R.ok(collectContentConnectPage);
    }

    /**
     * 更新收藏描述
     *
     * @param collectContentId 收藏目录id
     * @param description 要更新的收藏描述
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/6 8:31
     */
    @Override
    public R updateCollectDescription(Long collectContentId, String description) {
        LambdaUpdateWrapper<CollectContent> collectContentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        collectContentLambdaUpdateWrapper.eq(CollectContent::getId, collectContentId);
        collectContentLambdaUpdateWrapper.set(CollectContent::getDescription, description);
        collectContentMapper.update(null, collectContentLambdaUpdateWrapper);
        return R.ok();
    }

    /**
     * 更新收藏目录标题
     *
     * @param collectContentId 收藏目录id
     * @param name 要更新的收藏名称
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/6 8:35
     */
    @Override
    public R updateCollectName(Long collectContentId, String name) {
        LambdaUpdateWrapper<CollectContent> collectContentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        collectContentLambdaUpdateWrapper.eq(CollectContent::getId, collectContentId);
        collectContentLambdaUpdateWrapper.set(CollectContent::getName, name);
        collectContentMapper.update(null, collectContentLambdaUpdateWrapper);
        return R.ok();
    }

    /**
     * 删除收藏夹
     *
     * @param collectContentId 收藏目录id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/6 8:55
     */
    @Override
    public R deleteCollectContent(Long collectContentId) {
        return R.ok(collectContentMapper.deleteById(collectContentId));
    }

    /**
     * 设置收藏夹为私密收藏夹
     *
     * @param collectContentId 收藏目录id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/6 9:13
     */
    @Override
    public R setCollectPrivate(Long collectContentId) {
        LambdaUpdateWrapper<CollectContent> collectContentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        collectContentLambdaUpdateWrapper.eq(CollectContent::getId, collectContentId);
        collectContentLambdaUpdateWrapper.set(CollectContent::getIsPrivate, CommonEnum.COLLECT_IS_PRIVATE.getCode());
        collectContentMapper.update(null, collectContentLambdaUpdateWrapper);
        return R.ok();
    }

    /**
     * 设置收藏夹为公开收藏夹
     *
     * @param collectContentId 收藏目录id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/6 9:15
     */
    @Override
    public R setCollectPublic(Long collectContentId) {
        LambdaUpdateWrapper<CollectContent> collectContentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        collectContentLambdaUpdateWrapper.eq(CollectContent::getId, collectContentId);
        collectContentLambdaUpdateWrapper.set(CollectContent::getIsPrivate, CommonEnum.COLLECT_IS_PUBLIC.getCode());
        collectContentMapper.update(null, collectContentLambdaUpdateWrapper);
        return R.ok();
    }
}
