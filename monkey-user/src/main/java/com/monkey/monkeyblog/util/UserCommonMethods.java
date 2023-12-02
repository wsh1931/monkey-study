package com.monkey.monkeyblog.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyblog.mapper.UserFansMapper;
import com.monkey.monkeyblog.pojo.UserFans;

/**
 * @author: wusihao
 * @date: 2023/12/2 9:14
 * @version: 1.0
 * @description: 用户模块通用方法
 */
public class UserCommonMethods {

    /**
     * 判断当前登录用户是否是作者粉丝
     *
     * @param userId 作者id
     * @param nowUserId 当前登录用户id
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/2 9:15
     */
    public static int judgeIsFans(Long userId, String nowUserId, UserFansMapper userFansMapper) {
        LambdaQueryWrapper<UserFans> userFansLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userFansLambdaQueryWrapper.eq(UserFans::getUserId, userId);
        userFansLambdaQueryWrapper.eq(UserFans::getFansId, nowUserId);
        Long selectCount = userFansMapper.selectCount(userFansLambdaQueryWrapper);
        if (selectCount > 0) {
            return CommonEnum.IS_FANS.getCode();
        } else {
            return CommonEnum.NOT_FANS.getCode();
        }
    }

    /**
     * 判断改字符串是否全由英文字母组成
     *
     * @param str 用户传入的字符串
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/2 16:14
     */
    public static boolean isAllLetters(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
}
