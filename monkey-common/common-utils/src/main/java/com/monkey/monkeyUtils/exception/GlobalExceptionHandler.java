package com.monkey.monkeyUtils.exception;

import com.monkey.monkeyUtils.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //指定出现什么异常执行这个方法
    @ExceptionHandler(Exception.class)
    @ResponseBody //为了返回数据
    public R error(Exception e) {
        e.printStackTrace();
        return R.error(500, "发生了全局异常处理");
    }

    //特定异常
    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody //为了返回数据
    public R error(ArithmeticException e) {
        e.printStackTrace();
        return R.error(500, "执行了ArithmeticException异常处理..");
    }

    //自定义异常
    @ExceptionHandler(MonkeyBlogException.class)
    @ResponseBody //为了返回数据
    public R error(MonkeyBlogException e) {
        log.error(e.getMessage());
        e.printStackTrace();

        return R.error(e.getCode(), e.getMessage());
    }

}
