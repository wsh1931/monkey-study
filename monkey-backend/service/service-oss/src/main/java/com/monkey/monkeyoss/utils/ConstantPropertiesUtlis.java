package com.monkey.monkeyoss.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 常量属性读取的配置类

@Component
public class ConstantPropertiesUtlis implements InitializingBean {
    // 读取配置文件内容
    @Value("${aliyun.oss.file.endpoint}")
    private String endPoint;
    @Value("${aliyun.oss.file.keyid}")
    private String keyId;
    @Value("${aliyun.oss.file.keysecret}")
    private String keySecret;
    @Value("${aliyun.oss.file.bucketname}")
    private String bucketName;

    // 定义公开静态常量，方便外面的类调用
    public static String END_POINT;
    public static String KEY_ID;
    public static String KER_SECRET;
    public static String BUCKET_NAME;
    /*
     在初始化的时候执行这个方法
     因为属性类型是private，所以当项目启动，Spring加载之后，执行接口的一个方法，读取字段值
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = endPoint;
        KEY_ID = keyId;
        KER_SECRET = keySecret;
        BUCKET_NAME = bucketName;
    }
}
