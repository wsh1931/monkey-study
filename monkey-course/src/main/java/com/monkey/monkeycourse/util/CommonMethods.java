package com.monkey.monkeycourse.util;

/**
 * @author: wusihao
 * @date: 2023/8/24 15:05
 * @version: 1.0
 * @description: 常用方法类
 */
public class CommonMethods {

    /**
     * 通过传入的六位小数点的浮点数转化成两位小数点的浮点数
     *
     * @param oldFloat 原浮点数
     * @return {@link null}
     * @author wusihao
     * @date 2023/8/24 15:05
     */
    public static String getTwoFloatBySixFloat(Float oldFloat) {
        String str = String.valueOf(oldFloat);
        int index = str.indexOf('.');
        if (index != -1 && index + 3 <= str.length()) {
            return str.substring(0, index + 3);
        } else {
            return str;
        }
    }
}
