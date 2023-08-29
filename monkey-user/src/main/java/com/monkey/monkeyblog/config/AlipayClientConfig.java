package com.monkey.monkeyblog.config;


import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author: wusihao
 * @date: 2023/8/24 11:31
 * @version: 1.0
 * @description:
 */
@Configuration
public class AlipayClientConfig {

    // 支付宝支付
    // 应用Id

    @Value("${alipay.app-Id}")
    private String appId;

    @Value("${alipay.gateway-url}")
    private String gatewayUrl;

    @Value("${alipay.merchant-private-key}")
    private String merchantPrivateKey;

    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;

    // 密钥加签方式配置
    // 配置必填参数
    @Bean
    public AlipayClient alipayClient() {
        AlipayConfig alipayConfig = new AlipayConfig();
        //设置网关地址
        alipayConfig.setServerUrl(gatewayUrl);
        //设置应用APPID
        alipayConfig.setAppId(appId);
        //设置应用私钥(商户私钥)
        alipayConfig.setPrivateKey(merchantPrivateKey);
        //设置请求格式，固定值json
        alipayConfig.setFormat(AlipayConstants.FORMAT_JSON);
        //设置字符集
        alipayConfig.setCharset(AlipayConstants.CHARSET_UTF8);
        //设置支付宝公钥
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        //设置签名类型
        alipayConfig.setSignType(AlipayConstants.SIGN_TYPE_RSA2);

        AlipayClient alipayClient = null;
        try {
            //构造client
            alipayClient = new DefaultAlipayClient(alipayConfig);
            return alipayClient;
        }catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }
}
