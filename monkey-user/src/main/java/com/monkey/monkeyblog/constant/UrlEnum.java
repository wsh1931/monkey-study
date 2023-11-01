package com.monkey.monkeyblog.constant;

/**
 * @author: wusihao
 * @date: 2023/10/13 8:13
 * @version: 1.0
 * @description:
 */
public enum UrlEnum {
    VIP_PICTURE("vip图片地址", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/vip/vip.jpg")

    ;
    private String type;
    private String url;

    UrlEnum(String code, String url) {
        this.type = code;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
