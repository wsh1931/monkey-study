package com.monkey.monkeycommunity.util;

import java.text.DecimalFormat;

/**
 * @author: wusihao
 * @date: 2023/9/19 7:57
 * @version: 1.0
 * @description: 公共方法
 */
public class CommonMethod {
    /**
     * 将一个double类型的变量转化为一个百分比的数 比如21.12转化为21%
     *
     * @param number 待转化的数
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/19 7:58
     */
    public static Integer doubleToRate(Double number) {
        number *= 100;
        // 创建 DecimalFormat 对象，并设置格式为两位小数
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        // 使用 DecimalFormat 对象对 double 值进行格式化
        String formattedNumber = decimalFormat.format(number);
        return (int)Double.parseDouble(formattedNumber);
    }

    /**
     * 将double转化为保留一位小数的字符串
     *
     * @param number 待转化的数
     * @return {@link null}
     * @author wusihao
     * @date 2023/9/19 8:21
     */
    public static String doubleToOne(double number) {
        // 创建 DecimalFormat 对象，并设置格式为两位小数
        DecimalFormat decimalFormat = new DecimalFormat("0.0");

        // 使用 DecimalFormat 对象对 double 值进行格式化
        String formattedNumber = decimalFormat.format(number);
        return formattedNumber;
    }
}
