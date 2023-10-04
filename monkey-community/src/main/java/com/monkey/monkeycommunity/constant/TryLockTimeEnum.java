package com.monkey.monkeycommunity.constant;

public enum TryLockTimeEnum {

    SUCCESS_APPLICATION(3, "同意用户加入社区（秒）"),
    REFUSE_APPLICATION(3, "拒绝用户加入社区(秒)"),

    BATCH_SUCCESS_APPLICATION(5, "批量通过用户申请时间(秒)"),
    BATCH_REFUSE_APPLICATION(5, "批量拒绝用户申请时间(秒)"),
    BATCH_DELETE_APPLICATION(5, "批量删除用户申请时间(秒)")
    ;

    private Integer timeUnit;
    private String keyName;

    TryLockTimeEnum(Integer timeUnit, String keyName) {
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
