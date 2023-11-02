package com.monkey.monkeyblog.redis;

// -1表示时间永不过期
public enum RedisKeyAndExpireEnum {
    // 一级举报类型（天）
    ONE_REPORT_TYPE(1, "oneReportType："),
    // 二级举报类型 + 一级举报类型id(天)
    TWO_REPORT_TYPE(1, "twoReportType："),
    ;

    private Integer timeUnit;
    private String keyName;

    RedisKeyAndExpireEnum(Integer timeUnit, String keyName) {
        this.timeUnit = timeUnit;
        this.keyName = keyName;
    }

    public Integer getTimeUnit() {
        return timeUnit;
    }

    public String getKeyName() {
        return keyName;
    }
}
