package com.monkey.monkeyblog;

import com.monkey.monkeyUtils.util.DateSelfUtils;
import com.monkey.monkeyUtils.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;

/**
 * @author: wusihao
 * @date: 2023/12/14 10:32
 * @version: 1.0
 * @description:
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApplicationTest {
    @Test
    public void timeTest() {
        String currentYearFirstDay = DateUtils.getCurrentYearFirstDay(DateUtils.DATE_PATTERN);
        String nowDate = DateUtils.getNowDate(DateUtils.DATE_PATTERN);
        System.err.println(currentYearFirstDay);
        System.err.println(nowDate);
    }
}
