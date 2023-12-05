<template>
    <div class="MonkeyWebUserCollect-container">
        <el-row>
            <el-col :span="5">
                <el-menu
                    router
                    :default-active="defaultRouter"
                    class="el-menu-vertical-demo"
                    text-color="gray"
                    active-text-color="#409EFF">
                    <el-menu-item>
                        <i class="el-icon-folder-add"></i>
                        <span slot="title">新建收藏夹</span>
                    </el-menu-item>
                    <el-menu-item 
                    class="menu-item" 
                    v-for="i in 10" 
                    :key="i"
                    :index="`/user/center/collect/detail/${i}/${i}`">
                        <i class="el-icon-folder"></i>
                        <span slot="title" class="collect-title">新建收藏新建收藏夹新建收藏夹新建收藏夹夹</span>
                        <div class="collect-number">
                            <i class="el-icon-document"></i>
                            <span>11111</span>
                        </div>
                    </el-menu-item>
                </el-menu>
            </el-col>
            <el-col :span="19">
                <router-view></router-view>
            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebUserCollect',

    data() {
        return {
            defaultRouter: "",
        };
    },
    watch: {
        "$route.path"(path) {
            this.defaultRouter = path;
        },
    },
    
    created() {
        
    },

    methods: {
        
    },
};
</script>

<style scoped>
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
.el-icon-document {
    font-size: 12px;
    transform: scale(0.8);
}
.collect-number {
    position: absolute;
    bottom: 10px;
    right: 10px;
    font-size: 12px;
}
.menu-item {
    position: relative;
    box-shadow: 0 0 5px 0 rgba(128, 128, 128, 0.1);
}
.el-menu-item:nth-child(n + 2), .el-submenu__title:nth-child(n + 2) {
    height: 66px;
    line-height: normal;
    padding-top: 10px;
}
.collect-title {
    display: inline-block;
    white-space: nowrap;
    text-overflow: ellipsis;
    vertical-align: middle;
    overflow: hidden;
    max-width: 140px;
}
.el-menu-item {
    font-size: 12px;
}
.el-menu-vertical-demo {
    text-align: left;
    min-height: calc(100vh - 81px);
    height: calc(100vh - 200px);
    overflow-y: auto;
}
.file-navigate {
    background-color: #fff;
    padding: 16px;
}
</style>