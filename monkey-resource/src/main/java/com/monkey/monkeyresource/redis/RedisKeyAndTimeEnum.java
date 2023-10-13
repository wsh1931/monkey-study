package com.monkey.monkeyresource.redis;

public enum RedisKeyAndTimeEnum {
    NOT_ENUM(-1, "不存在该字段"),
    ;

    private Integer timeUnit;
    private String keyName;

    RedisKeyAndTimeEnum(Integer timeUnit, String keyName) {
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
