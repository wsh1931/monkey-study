package com.monkey.monkeyUtils.constants;

/**
 * @author: wusihao
 * @date: 2023/11/27 16:24
 * @version: 1.0
 * @description: 默认图片枚举类
 */
public enum DefaultPictureEnum {
    // 未定义该枚举类
    NOT_ENUM("unknown", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-no-file-64.png"),
    COMMUNITY_ARTICLE("社区文章默认图片", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/defaultPicture/community-article.jpg")
            ;
    private String type;
    private String url;

    DefaultPictureEnum(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    //获取指定值枚举类
    public static DefaultPictureEnum getFileUrlByFileType(String type) {
        //code为null
        if (null == type) {
            return DefaultPictureEnum.NOT_ENUM;
        }
        DefaultPictureEnum[] values = DefaultPictureEnum.values();
        for (DefaultPictureEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        //没找到、
        return DefaultPictureEnum.NOT_ENUM;
    }
}
