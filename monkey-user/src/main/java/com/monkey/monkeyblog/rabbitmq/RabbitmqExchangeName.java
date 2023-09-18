package com.monkey.monkeyblog.rabbitmq;

public class RabbitmqExchangeName {
    // 验证码
    public static final String EMAIL_CODE_EXCHANGE = "emailCode";

    // 用户添加直连交换机
    public static final String userInsertDirectExchange = "userInsertDirectExchange";
    // 用户添加直连死信交换机
    public static final String userInsertDixDirectExchange = "userInsertDixDirectExchange";

    // 用户更新直连交换机
    public static final String userUpdateDirectExchange = "userUpdateDirectExchange";
    // 用户更新直连死信交换机
    public static final String userUpdateDlxDirectExchange = "userUpdateDlxDirectExchange";

    // 用户删除直连交换机
    public static final String userDeleteDirectExchange = "userDeleteDirectExchange";
    // 用户删除死信直连交换机
    public static final String userDeleteDlxDirectExchange = "userDeleteDlxDirectExchange";
}
