package com.monkey.monkeyblog.rabbitmq;

public class RabbitmqRoutingName {
    // 验证码
    public static final String EMAIL_CODE = "email_code";
    // 验证码死信路由key
    public static final String EMAIL_CODE_DLX = "email_code_dlx";

    // 用户订单路由
    public static final String userOrderRouting = "userOrderRouting";
    // 用户死信订单路由
    public static final String userDlxOrderRouting = "userDlxOrderRouting";

    // 用户添加路由
    public static final String userInsertRouting = "userInsertRouting";
    // 用户添加死信路由
    public static final String userInsertDlxRouting = "userInsertDlxRouting";

    // 用户更新路由
    public static final String userUpdateRouting = "userUpdateRouting";
    // 用户更新死信路由
    public static final String userUpdateDlxRouting = "userUpdateDlxRouting";

    // 用户删除路由
    public static final String userDeleteRouting = "userDeleteRouting";
    // 用户删除死信队列
    public static final String userDeleteDlxRouting = "userDeleteDlxRouting";

}
