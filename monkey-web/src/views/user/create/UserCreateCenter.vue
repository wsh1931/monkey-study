<template>
    <div class="MonkeyWebCreateCenter-container">
        <el-row>
            <el-col :span="4" class="left-header">
                <div @mouseover="isShowUpload = true">
                    <el-button 
                    class="publish-button" 
                    type="primary" >
                    <span>发布&nbsp;</span>
                    <i class="el-icon-caret-bottom"></i>
                    </el-button>
                </div>
                <div 
                @mouseover="isShowUpload = true"
                @mouseleave="isShowUpload = false"
                class="show-publish-content" 
                v-if="isShowUpload">
                    <div class="publish-content-common">
                        <i class="iconfont icon-wenzhang publish-content-icon">&nbsp;</i>
                        <span>发布文章</span>
                    </div>
                    <div class="publish-content-common">
                        <i class="iconfont icon-icon_tiwen publish-content-icon">&nbsp;</i>
                        <span>发布问答</span>
                    </div>
                    <div class="publish-content-common">
                        <i class="iconfont icon-shequ1 publish-content-icon">&nbsp;</i>
                        <span>创建社区</span>
                    </div>
                    <div class="publish-content-common">
                        <i class="iconfont icon-ziyuan publish-content-icon">&nbsp;</i>
                        <span>上传资源</span>
                    </div>
                    <div class="publish-content-common">
                        <i class="iconfont icon-kecheng- publish-content-icon">&nbsp;</i>
                        <span>发布课程</span>
                    </div>
                </div>


                <!-- 创作中心导航栏 -->
                    <el-menu
                        router
                        :default-active="defaultActive"
                        class="el-menu-vertical-demo">
                        <el-menu-item index="/user/create/home">
                            <i class="el-icon-s-home"></i>
                            <span slot="title">首页</span>
                        </el-menu-item>
                        <el-submenu index="/user/create/manage">
                            <template slot="title">
                            <i class="el-icon-s-management"></i>
                            <span>管理</span>
                            </template>
                            <el-menu-item index="/user/create/manage/content">内容管理</el-menu-item>
                            <el-menu-item index="/user/create/manage/comment">评论管理</el-menu-item>
                            <el-menu-item index="/user/create/manage/barrage">弹幕管理</el-menu-item>
                        </el-submenu>
                        
                        <el-submenu index="1">
                            <template slot="title">
                            <i class="el-icon-s-data"></i>
                            <span>数据</span>
                            </template>
                            <el-menu-item index="1-1">作品数据</el-menu-item>
                            <el-menu-item index="1-2">收益数据</el-menu-item>
                            <el-menu-item index="1-3">粉丝数据</el-menu-item>
                        </el-submenu>
                        <el-submenu index="1">
                            <template slot="title">
                            <i class="el-icon-warning"></i>
                            <span>举报</span>
                            </template>
                            <el-menu-item index="1-1">与我有关</el-menu-item>
                            <el-menu-item index="1-2">收益数据</el-menu-item>
                        </el-submenu>
                    </el-menu>
            </el-col>
            <el-col :span="20">
                <router-view class="children-class"></router-view>
            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebCreateCenter',

    data() {
        return {
            // 是否显示上传信息
            isShowUpload: false,
            defaultActive: "", 
        };
    },

    created() {
        this.defaultActive = this.$route.path
    },
};
</script>

<style scoped>
.children-class {
    z-index: 1;
    height: calc(100vh - 93px);
}
::-webkit-scrollbar {
    width: 10px;
    background-color: #fff;
}

:hover ::-webkit-scrollbar-track-piece {
    background-color: #fff;
    
    border-radius: 6px;
    
}

:hover::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0, 0, 0, 0.1);
}

:hover::-webkit-scrollbar-thumb:vertical {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
}
.el-menu-vertical-demo {
    height: calc(100vh - 121px);
    overflow: auto;
}
.publish-content-icon {
    font-size: 18px;
    color: #409EFF;
}
.publish-content-common {
    padding: 10px 20px;
}
.publish-content-common:hover {
    cursor: pointer;
    color: #409EFF;
    background-color: #F1F3F7;
    
}
.show-publish-content {
    z-index: 2;
    position: absolute;
    right: -108px;
    top: 0;
    background-color: #fff;
    padding: 10px 0 10px 0;
    border-radius: 10px;
}
.left-header {
    position: relative;
    background-color: #fff;
}
.MonkeyWebCreateCenter-container {
    margin: 0px auto;
    width: 1200px;
    margin-top: 20px;
}
.publish-button {
    width: 100%;
}
</style>