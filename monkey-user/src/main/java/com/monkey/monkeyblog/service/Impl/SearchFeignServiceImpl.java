package com.monkey.monkeyblog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyblog.mapper.UserFansMapper;
import com.monkey.monkeyblog.pojo.UserFans;
import com.monkey.monkeyblog.service.SearchFeignService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * @author: wusihao
 * @date: 2023/11/14 10:02
 * @version: 1.0
 * @description:
 */
@Service
public class SearchFeignServiceImpl implements SearchFeignService {
    @Resource
    private UserFansMapper userFansMapper;

    /**
     * 得到所有用户粉丝信息
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/14 11:35
     */
    @Override
    public R queryAllUserFansInfo() {
        // 得到用户所有粉丝数
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.groupBy("user_id")
                .select("count(fans_id) as fansCount", "user_id");
        List<Map<String, Object>> userFansInfo = userFansMapper.selectMaps(userFansQueryWrapper);

        return R.ok(userFansInfo);
    }

    /**
     * 判断当前登录用户与作者是否为粉丝
     *
     * @param userId 当前登录用户id
     * @param authorId 作者id
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/15 9:45
     */
    @Override
    public R judgeIsFans(String userId, String authorId) {
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("user_id", authorId);
        userFansQueryWrapper.eq("fans_id", userId);
        Long selectCount = userFansMapper.selectCount(userFansQueryWrapper);
        return R.ok(selectCount);
    }
}
