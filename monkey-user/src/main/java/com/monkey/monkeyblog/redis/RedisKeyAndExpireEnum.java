package com.monkey.monkeyblog.redis;

// -1表示时间永不过期
public enum RedisKeyAndExpireEnum {
    // 一级举报类型（天）
    ONE_REPORT_TYPE(1, "oneReportType："),
    // 二级举报类型 + 一级举报类型id(天)
    TWO_REPORT_TYPE(1, "twoReportType："),

    // 用户邮箱验证的验证码
    UPDATE_EMAIL_VERIFY(10, "update_email_verify_code ==> userId = "),
    // 用户邮箱验证成功获得的授权时间(分钟)
    USER_EMAIL_VERIFY_SUCCESS(10, "user_email_verity_success ==> userId = "),

    // 用户绑定邮箱验证码
    USER_BIND_EMAIL_VERIFY(10, "user_bind_email_verify ==> userId = "),

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
