package com.monkey.monkeyquestion.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqRoutingName {

    // 问答添加路由
    public static final String questionInsertRouting = "questionInsertRouting";
    // 问答添加死信路由
    public static final String questionInsertDlxRouting = "questionInsertDlxRouting";

    // 问答更新路由
    public static final String questionUpdateRouting = "questionUpdateRouting";
    // 问答更新死信路由
    public static final String questionUpdateDlxRouting = "questionUpdateDlxRouting";

    // 问答删除路由
    public static final String questionDeleteRouting = "questionDeleteRouting";
    // 问答删除死信队列
    public static final String questionDeleteDlxRouting = "questionDeleteDlxRouting";
}
