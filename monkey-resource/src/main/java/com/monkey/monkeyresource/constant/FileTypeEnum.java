package com.monkey.monkeyresource.constant;

/**
 * @author: wusihao
 * @date: 2023/10/8 10:16
 * @version: 1.0
 * @description:
 */
public enum FileTypeEnum {
    // 未定义该枚举类
    NOT_ENUM("unknown", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-no-file-64.png"),
    BMP("bmp", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-bmp-file-64.png"),
    CSV("csv", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-csv-file-64.png"),
    DATABASE("database", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-database-50.png"),
    DOC("doc", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-doc-file-64.png"),
    DOCX("docx", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-docx-file-64.png"),
    GIT("git", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-gif-file-64.png"),
    JPEG("jpeg", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-jpeg-64.png"),
    JPG("jpg", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-jpg-file-64.png"),
    MKV("mkv", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-mkv-64.png"),
    MP3("mp3", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-mp3-file-64.png"),
    MP4("mp4", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-mp4-file-64.png"),
    PDF("pdf", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-pdf-64.png"),
    PNG("png", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-png-file-64.png"),
    PPT("ppt", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-ppt-file-64.png"),
    RAR("rar", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-rar-file-64.png"),
    TAR("tar", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-tar-64.png"),
    TXT("txt", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-txt-file-64.png"),
    WAV("wav", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-wav-file-64.png"),
    WMV("wmv", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-wmv-64.png"),
    XLS("xls", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-xls-file-64.png"),
    XLSX("xlsx", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-xlsx-file-64.png"),
    ZIP("zip", "https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-zip-file-64.png")
    ;
    private String type;
    private String url;

    FileTypeEnum(String type, String url) {
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
    public static FileTypeEnum getFileUrlByFileType(String type) {
        //code为null
        if (null == type) {
            return FileTypeEnum.NOT_ENUM;
        }
        FileTypeEnum[] values = FileTypeEnum.values();
        for (FileTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        //没找到、
        return FileTypeEnum.NOT_ENUM;
    }
}
