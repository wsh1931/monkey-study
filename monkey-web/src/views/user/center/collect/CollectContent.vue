<template>
    <div class="MonkeyWebCollectContent-container">
        <div v-if="!showEditName" style="margin-bottom: 16px;">
            <div class="collect-title">{{ collectContent.name }}</div>
            <i @click="showEditNameInput(collectContent.name)" class="el-icon-edit-outline"></i>
        </div>
        <div v-if="showEditName" style="margin-bottom: 16px;">
            <el-input 
            v-model="edit.name"
            placeholder="请输入收藏标题"
            class="title-edit"
            size="mini"
            ></el-input>
            <i 
            @click="showEditName = false"
            class="el-icon-close icon-close"></i>
            <i class="el-icon-check icon-open"></i>
        </div>
        <div class="collect-brief-operation">
            <span v-if="!showEditDescription">
                <div class="collect-brief">{{ collectContent.description }}</div>
                <i @click="showEditDescriptionInput(collectContent.description)" class="el-icon-edit-outline"></i>
            </span>
            <span v-if="showEditDescription">
                <el-input 
                v-model="edit.description"
                placeholder="请输入收藏简介"
                class="title-edit"
                size="mini"
                ></el-input>
                <i
                @click="showEditDescription = false"
                class="el-icon-close icon-close"></i>
                <i class="el-icon-check icon-open"></i>
            </span>
            <div class="operate">
                <span class="delete-collect">删除收藏夹</span>
                <span class="private-collect">设为私密</span>
            </div>
        </div>
        <div class="divisor"></div>
        <!-- 收藏夹内容 -->
        <div style="position: relative;">
            <el-input
            size="small"
            suffix-icon="el-icon-search"
            class="search-collect"
            placeholder="请输入关键词"></el-input>
        </div>
        
        <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="全部" name="all"></el-tab-pane>
            <el-tab-pane label="资源" name="resource"></el-tab-pane>
            <el-tab-pane label="社区文章" name="communityArticle"></el-tab-pane>
            <el-tab-pane label="课程" name="course"></el-tab-pane>
            <el-tab-pane label="问答" name="question"></el-tab-pane>
            <el-tab-pane label="博客" name="article"></el-tab-pane>
        </el-tabs>
        <div class="content-card">
            <div 
                v-for="i in 10" :key="i"
                class="collect-content-card">
                <el-tag 
                effect="plain"
                class="collect-connect-type"
                type="info" 
                size="mini">博客</el-tag>
                <span class="collect-content-title">收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题收藏标题</span>
                <span style="vertical-align: middle;" class="iconfont icon-shoucang"></span>
            </div>
        </div>
        
            <PagiNation 
            class="pagination"
            :totals="totals" 
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
    </div>
</template>

<script>
import PagiNation from "@/components/pagination/PagiNation.vue";
export default {
    name: 'MonkeyWebCollectContent',
    components: {
        PagiNation
    },
    data() {
        return {
            currentPage: 0,
            pageSize: 10,
            totals: 0,
            activeName: "all",
            showEditName: false,
            showEditDescription: false,
            collectContent: {
                name: "文件夹名文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称文件夹名称称",
                description: "文件夹文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介文件夹简介简介"
            },
            edit: {
                name: "",
                description: "",
            },
            collectContentList: [],
        };
    },

    mounted() {
        
    },

    methods: {
        handleSizeChange(val) {
            this.pageSize = val;
        },
        handleCurrentChange(val) {
            this.currentPage = val;
        },
        // 显示收藏描述输入框
        showEditDescriptionInput(description) {
            this.showEditDescription = true;
            this.edit.description = JSON.parse(JSON.stringify(description));
        },
        // 显示名称编辑框
        showEditNameInput(name) {
            this.showEditName = true;
            this.edit.name = JSON.parse(JSON.stringify(name));
        }
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

.content-card {
    padding: 5px;
    height: 475px;
    overflow-y: auto;
}
.pagination {
    text-align: right;
}
.collect-content-card:nth-child(n + 1) {
    margin-bottom: 10px;
}
.collect-content-card:last-child {
    margin-bottom: 0;
}
.collect-content-card {
    padding: 16px;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: 0.2s linear all;
}
.collect-content-card:hover {
    box-shadow: 0 0 10px grey;
}
.collect-connect-type {
    margin-right: 5px;
    vertical-align: middle;
}
.collect-content-title:hover {
    color: #409EFF;
}
.collect-content-title {
    display: inline-block;
    color: gray;
    white-space: nowrap;
    text-overflow: ellipsis;
    vertical-align: middle;
    overflow: hidden;
    max-width: 588px;
    margin-right: 5px;
}
.search-collect {
    width: 200px;
    position: absolute;
    top: 0;
    right: 0;
    z-index: 10000;
}
.divisor {
    height: 1px;
    background-color: rgba(0, 0, 0, 0.1);
    margin-bottom: 10px;
}
.title-edit {
    width: 300px;
    margin-right: 10px;
}
.icon-open:hover {
    color: #409EFF;
}
.icon-open {
    cursor: pointer;
    font-size: 20px;
    transition: 0.2s linear all;
}
.icon-close:hover {
    color: #F56C6C;
}
.icon-close {
    margin-right: 10px;
    cursor: pointer;
    font-size: 20px;
    transition: 0.2s linear all;
}
.el-icon-edit-outline {
    cursor: pointer;
}
.private-collect:hover {
    opacity: 0.8;
}
.private-collect {
    color: #409EFF;
    cursor: pointer;
}
.delete-collect:hover {
    opacity: 0.8;
}
.delete-collect {
    cursor: pointer;
    color: #F56C6C;
    margin-right: 10px;
}
.operate {
    position: absolute;
    right: 0;
    top: 0;
}
.collect-brief-operation {
    position: relative;
    margin-bottom: 16px;
}
.collect-brief {
    display: inline-block;
    color: gray;
    white-space: nowrap;
    text-overflow: ellipsis;
    vertical-align: middle;
    overflow: hidden;
    max-width: 400px;
    margin-right: 10px;
}
.collect-title {
    display: inline-block;
    color: gray;
    font-weight: 550;
    white-space: nowrap;
    text-overflow: ellipsis;
    vertical-align: middle;
    overflow: hidden;
    max-width: 620px;
    margin-right: 10px;
}
.MonkeyWebCollectContent-container {
    padding: 20px;
    background-color: #fff;
    font-size: 14px;
    vertical-align: middle;
    min-height: calc(100vh - 121px);
}
</style>