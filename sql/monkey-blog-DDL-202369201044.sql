DROP TABLE IF EXISTS article;
CREATE TABLE article(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '文章id' ,
    `user_id` BIGINT    COMMENT '文章所属的用户id' ,
    `title` VARCHAR(30)    COMMENT '文章标题' ,
    `content` TEXT    COMMENT '文章内容' ,
    `visit` BIGINT   DEFAULT 0 COMMENT '文章游览数' ,
    `likes` BIGINT   DEFAULT 0 COMMENT '文章点赞数' ,
    `photo` TEXT    COMMENT '文章图片链接' ,
    `profile` VARCHAR(100)    COMMENT '文章简介' ,
    `create_time` DATETIME    COMMENT '创建时间' ,
    `update_time` DATETIME    COMMENT '修改时间' ,
    PRIMARY KEY (id)
)  COMMENT = '用户发表文章表';

DROP TABLE IF EXISTS article_label;
CREATE TABLE article_label(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '' ,
    `article_id` BIGINT    COMMENT '文章id' ,
    `label_id` BIGINT    COMMENT '标签id' ,
    PRIMARY KEY (id)
)  COMMENT = '文章与标签对应表';

DROP TABLE IF EXISTS chat_history;
CREATE TABLE chat_history(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '主键id' ,
    `sender_id` BIGINT    COMMENT '发送者id' ,
    `receiver_id` BIGINT    COMMENT '接收者id' ,
    `create_time` DATETIME    COMMENT '发送时间' ,
    `content` TEXT    COMMENT '' ,
    PRIMARY KEY (id)
)  COMMENT = '聊天记录表';

DROP TABLE IF EXISTS comment;
CREATE TABLE comment(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '' ,
    `user_id` BIGINT    COMMENT '评论者id' ,
    `article_id` BIGINT    COMMENT '该评论属于哪篇文章' ,
    `content` VARCHAR(1000)    COMMENT '评论内容' ,
    `parent_id` BIGINT   DEFAULT 0 COMMENT '父评论' ,
    `reply_id` BIGINT    COMMENT '回复者id' ,
    `comment_time` DATETIME    COMMENT '评论时间' ,
    PRIMARY KEY (id)
)  COMMENT = '发表评论表';

DROP TABLE IF EXISTS comment_like;
CREATE TABLE comment_like(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '主键id' ,
    `article_id` BIGINT    COMMENT '文章id' ,
    `user_id` BIGINT    COMMENT '用户id' ,
    `comment_id` BIGINT    COMMENT '评论id' ,
    `create_time` DATETIME    COMMENT '创建时间' ,
    PRIMARY KEY (id)
)  COMMENT = '文章与评论点赞关系表';

DROP TABLE IF EXISTS label;
CREATE TABLE label(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '标签id' ,
    `label_name` VARCHAR(255)    COMMENT '标签名称' ,
    PRIMARY KEY (id)
)  COMMENT = '标签表';

DROP TABLE IF EXISTS recent_visit_userhome;
CREATE TABLE recent_visit_userhome(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '' ,
    `visit_id` BIGINT    COMMENT '访问人id' ,
    `be_visit_id` BIGINT    COMMENT '被访问人id' ,
    `create_time` DATETIME    COMMENT '访问时间' ,
    PRIMARY KEY (id)
)  COMMENT = '最近访问用户主页表';

DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '主键id' ,
    `dict_sort` INT    COMMENT '字典排序' ,
    `dict_label` VARCHAR(30)    COMMENT '字典标签' ,
    `dict_value` INT    COMMENT '字典值' ,
    `dict_type` VARCHAR(30)    COMMENT '字典类型' ,
    `status` INT   DEFAULT 0 COMMENT '状态' ,
    PRIMARY KEY (id)
)  COMMENT = '数据字典内容';

DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '字典主键' ,
    `dict_name` VARCHAR(30)    COMMENT '字典名称' ,
    `dict_type` VARCHAR(30)    COMMENT '字典类型' ,
    `status` INT   DEFAULT 0 COMMENT '状态' ,
    PRIMARY KEY (id)
)  COMMENT = '数据字典类型';

DROP TABLE IF EXISTS user;
CREATE TABLE user(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '用户id' ,
    `password` VARCHAR(255)    COMMENT '用户密码' ,
    `username` VARCHAR(100)   DEFAULT '猴子' COMMENT '用户名' ,
    `job` VARCHAR(20)    COMMENT '用户职位' ,
    `photo` VARCHAR(255)    COMMENT '用户头像' ,
    `brief` VARCHAR(255)   DEFAULT '这个人很懒，什么都没留下' COMMENT '用户简介' ,
    `register_time` DATETIME    COMMENT '用户注册时间' ,
    `is_deleted` INT   DEFAULT 0 COMMENT '用户是否被删除(0表示未删除，1表示以删除)' ,
    `job_unit` VARCHAR(30)    COMMENT '工作单位' ,
    PRIMARY KEY (id)
)  COMMENT = '用户信息表';

DROP TABLE IF EXISTS user_collect;
CREATE TABLE user_collect(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '' ,
    `user_id` BIGINT    COMMENT '用户id' ,
    `article_id` BIGINT    COMMENT '文章id' ,
    `create_time` DATETIME    COMMENT '收藏时间' ,
    PRIMARY KEY (id)
)  COMMENT = '用户收藏关系表';

DROP TABLE IF EXISTS user_fans;
CREATE TABLE user_fans(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '' ,
    `user_id` BIGINT    COMMENT '用户id' ,
    `fans_id` BIGINT    COMMENT '文章id' ,
    `create_time` DATETIME    COMMENT '关注时间' ,
    PRIMARY KEY (id)
)  COMMENT = '用户关注关系表';

DROP TABLE IF EXISTS user_like;
CREATE TABLE user_like(
    `id` BIGINT NOT NULL AUTO_INCREMENT  COMMENT '' ,
    `user_id` BIGINT    COMMENT '用户id' ,
    `article_id` BIGINT    COMMENT '文章id' ,
    `create_time` DATETIME    COMMENT '点赞时间' ,
    PRIMARY KEY (id)
)  COMMENT = '用户点赞关系表';

