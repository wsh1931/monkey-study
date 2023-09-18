package com.monkey.monkeyblog.rabbitmq;

public class RabbitmqQueueName {

    // 验证码
    public static final String EMAIL_CODE_QUEUE = "email_code_queue";
    // 验证码死信队列
    public static final String EMAIL_CODE_DLX_QUEUE = "email_code_dlx_queue";

    // 添加队列
    public static final String userInsertQueue = "userInsertQueue";
    // 添加死信队列
    public static final String userInsertDlxQueue = "userInsertDlxQueue";

    // 更新队列
    public static final String userUpdateQueue = "userUpdateQueue";
    // 更新死信队列
    public static final String userUpdateDlxQueue = "userUpdateDlxQueue";

    // 删除队列
    public static final String userDeleteQueue = "userDeleteQueue";
    // 删除死信队列
    public static final String userDeleteDlxQueue = "userDeleteDlxQueue";
}
