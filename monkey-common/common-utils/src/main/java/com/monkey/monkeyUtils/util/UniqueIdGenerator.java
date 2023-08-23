package com.monkey.monkeyUtils.util;

import java.util.concurrent.atomic.AtomicLong;

public class UniqueIdGenerator {
    private static final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    // 得到用不重复的long型变量
    public static long generateUniqueId() {
        return counter.incrementAndGet();
    }
}