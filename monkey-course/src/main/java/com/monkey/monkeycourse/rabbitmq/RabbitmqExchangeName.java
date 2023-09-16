package com.monkey.monkeycourse.rabbitmq;

/**
 * @author: wusihao
 * @date: 2023/9/11 16:41
 * @version: 1.0
 * @description:
 */
public class RabbitmqExchangeName {

    // 课程视频弹幕
    public static final String COURSE_BARRAGE_EXCHANGE = "courseVideoBarrageExchange";

    // 课程视频弹幕死信交换机
    public static final String COURSE_BARRAGE_DLX_EXCHANGE = "courseVideoBarrageDlxExchange";
    

    // 课程交换机
    public static final String COURSE_DIRECT_EXCHANGE = "courseDirectExchange";

    // 社区添加直连交换机
    public static final String courseInsertDirectExchange = "courseInsertDirectExchange";
    // 社区添加直连死信交换机
    public static final String courseInsertDixDirectExchange = "courseInsertDixDirectExchange";

    // 课程更新直连交换机
    public static final String courseUpdateDirectExchange = "courseUpdateDirectExchange";
    // 课程更新直连死信交换机
    public static final String courseUpdateDlxDirectExchange = "courseUpdateDlxDirectExchange";

    // 课程删除直连交换机
    public static final String courseDeleteDirectExchange = "courseDeleteDirectExchange";
    // 课程删除死信直连交换机
    public static final String courseDeleteDlxDirectExchange = "courseDeleteDlxDirectExchange";
}
