package com.monkey.monkeyquestion.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqRoutingName {

    // 社区添加路由
    public static final String questionInsertRouting = "questionInsertRouting";
    // 社区添加死信路由
    public static final String questionInsertDlxRouting = "questionInsertDlxRouting";

    // 社区更新路由
    public static final String questionUpdateRouting = "questionUpdateRouting";
    // 社区更新死信路由
    public static final String questionUpdateDlxRouting = "questionUpdateDlxRouting";

    // 社区删除路由
    public static final String questionDeleteRouting = "questionDeleteRouting";
    // 社区删除死信队列
    public static final String questionDeleteDlxRouting = "questionDeleteDlxRouting";
}
