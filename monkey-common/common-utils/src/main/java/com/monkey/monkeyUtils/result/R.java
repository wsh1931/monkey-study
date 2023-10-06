/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.monkey.monkeyUtils.result;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.io.Serializable;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 失败
     */
    public static final int Error = 500;


    private int code;

    private String msg;

    private T data;

    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, "操作成功，请刷新页面或等待数据更新");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功，请刷新页面或等待数据更新");
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> R<T> error() {
        return restResult(null, Error, "操作失败, 请重试");
    }

    public static <T> R<T> error(String msg) {
        return restResult(null, Error, msg);
    }

    public static <T> R<T> error(T data) {
        return restResult(data, Error, "操作失败, 请重试");
    }

    public static <T> R<T> error(T data, String msg) {
        return restResult(data, Error, msg);
    }

    public static <T> R<T> error(int code, String msg) {
        return restResult(null, code, msg);
    }

    public static <T> R<T> error(T data, int code, String msg) {
        return restResult(data, code, msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public <T> T getData(TypeReference<T> typeReference) {
        String s = JSON.toJSONString(data);
        T t = JSON.parseObject(s, typeReference);
        return t;
    }

    public T getData() {
        return data;
    }


    public void setData(T data) {
        this.data = data;
    }
}

