package com.monkey.monkeyquestion.constant;

/**
 * @author: wusihao
 * @date: 2023/10/8 10:16
 * @version: 1.0
 * @description:
 */
public enum QuestionPictureEnum {
    // 未定义该枚举类
    QUESTION_DEFAULT_PIRCUTR("问答默认图片", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/question/780.jpg")
    ;
    private String type;
    private String url;

    QuestionPictureEnum(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

}
