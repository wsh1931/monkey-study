package com.monkey.monkeyresource.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 常量属性读取的配置类

@Component
public class ConstantPropertiesUtlis implements InitializingBean {

    // 支付宝支付
    // 应用Id

    @Value("${alipay.app-Id}")
    private String appId;

    @Value("${alipay.gateway-url}")
    private String gatewayUrl;

    @Value("${alipay.seller-id}")
    private String sellerId;

    @Value("${alipay.content-key}")
    private String contentKey;

    @Value("${alipay.merchant-private-key}")
    private String merchantPrivateKey;

    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    @Value("${alipay.return-url}")
    private String aliPayReturnUrl;

    @Value("${alipay.notify-url}")
    private String notifyUrl;

    // 支付宝支付
    // 应用Id
    public static String APP_ID;

    // 支付宝网关
    public static String GATEWAY_URL;

    // 商户PID， 卖家支付宝账号ID
    public static String SELLER_ID;

    // 接口内容加密密钥, 对称密钥
    public static String CONTENT_KEY;

    // 商户私钥
    public static String MERCHANT_PRIVATE_KEY;

    // 支付宝公钥
    public static String ALIPAY_PUBLIC_KEY;

    // 请求成功后的返回地址
    public static String ALIPAY_RETURN_URL;

    // 支付后的异步请求地址
    public static String NOTIFY_URL;
    /*
     在初始化的时候执行这个方法
     因为属性类型是private，所以当项目启动，Spring加载之后，执行接口的一个方法，读取字段值
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        APP_ID = appId;
        GATEWAY_URL = gatewayUrl;
        SELLER_ID = sellerId;
        CONTENT_KEY = contentKey;
        MERCHANT_PRIVATE_KEY = merchantPrivateKey;
        ALIPAY_PUBLIC_KEY = alipayPublicKey;
        ALIPAY_RETURN_URL = aliPayReturnUrl;
        NOTIFY_URL = notifyUrl;
    }
}
