<template>
    <div class="MonkeyWebCommunityInfo-container">
        <div class="header">
            <div>
                <img class="community-img" src="https://ts3.cn.mm.bing.net/th?id=OIP-C.bVb769JBdzVZYuksxZ2Y-AHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                <span class="community-name">啥也不会啥也不会啥也不会啥也不会啥也不会啥也不会啥也不会啥也不会啥也不会</span>
            </div>
            <el-row style="text-align: center; margin-top: 16px;">
                <el-col :span="11" class="community-member-count">
                    <el-row>2</el-row>
                    <el-row class="community-member-content">社区成员</el-row>
                </el-col>
                <el-col :span="2">&nbsp;</el-col>
                <el-col :span="11" class="community-content-count">
                    <el-row>2</el-row>
                    <el-row class="community-member-content">文章总数</el-row>
                </el-col>
            </el-row>
            <el-row class="community-function" >
                <el-col :span="6">
                    <div class="publish-article">
                        <div class="el-icon-s-promotion"></div>
                        <div>发布帖子</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="publish-article">
                        <div class="el-icon-s-custom"></div>
                        <div>创建社区</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="publish-article">
                        <div class="el-icon-s-data"></div>
                        <div>成员排行</div>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="publish-article">
                        <div class="el-icon-s-home"></div>
                        <div>社区主页</div>
                    </div>
                </el-col>
                
            </el-row>

            <div>
                <span class="community-label">音视频</span>
                <span class="community-label">音视频</span>
                <span class="community-label">音视频</span>
                <span class="community-label">音视频</span>
                <span class="community-label">音视频</span>
            </div>

            <el-row class="divider"></el-row>
            <div style="font-size: 14px;">
                <div>社区描述</div>
                <div class="community-description">社区描述社区描述社区描述社区描述社区描述社区描述社区描述社区描述社区描述社区描述社区描述社区描述社区描述社区描述社区描述社区描述</div>
                <div style="margin-bottom: 10px;">社区管理员</div>
                <div>
                    <el-tooltip class="item" effect="dark" content="Top Center 提示文字" placement="top">
                        <img class="manager-img" src="https://ts1.cn.mm.bing.net/th?id=OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                    </el-tooltip>
                    <el-tooltip class="item" effect="dark" content="Top Center 提示文字" placement="top">
                        <img class="manager-img" src="https://ts1.cn.mm.bing.net/th?id=OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                    </el-tooltip>
                    <el-tooltip class="item" effect="dark" content="Top Center 提示文字" placement="top">
                        <img class="manager-img" src="https://ts1.cn.mm.bing.net/th?id=OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                    </el-tooltip>
                    <el-tooltip class="item" effect="dark" content="Top Center 提示文字" placement="top">
                        <img class="manager-img" src="https://ts1.cn.mm.bing.net/th?id=OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                    </el-tooltip>
                    <el-tooltip class="item" effect="dark" content="Top Center 提示文字" placement="top">
                        <img class="manager-img" src="https://ts1.cn.mm.bing.net/th?id=OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                    </el-tooltip>
                    <el-tooltip class="item" effect="dark" content="Top Center 提示文字" placement="top">
                        <img class="manager-img" src="https://ts1.cn.mm.bing.net/th?id=OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&dpr=1.3&pid=3.1&rm=2" alt="">
                    </el-tooltip>

                    
                </div>
                <div style="text-align: center;">
                    <el-button type="info" round plain size="mini" class="management-button">管理</el-button>
                </div>

                <el-tabs v-model="activeName" @tab-click="handleClick">
                    <el-tab-pane label="积分" name="first">用户管理</el-tab-pane>
                    <el-tab-pane label="游览" name="second">配置管理</el-tab-pane>
                    <el-tab-pane label="点赞" name="third">角色管理</el-tab-pane>
                    <el-tab-pane label="收藏" name="third">角色管理</el-tab-pane>
                    
                </el-tabs>
                <div style="text-align: center;">
                    <el-button type="info" round plain size="mini" class="management-button">查看更多社区榜单</el-button>
                </div>

                <div style="margin-top: 10px;">
                    <div style="font-weight: bold;">社区公告</div>
                    <div style="color: gray; margin-top: 5px;">啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebCommunityInfo',

    data() {
        return {
            
        };
    },

    mounted() {
        
    },

    methods: {
        
    },
};
</script>

<style scoped>
.community-description {
    font-size: 13px;
    color: gray;
    margin-bottom: 10px;
}
.management-button {
    width: 100%;
    margin-top: 10px;
}
.manager-img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin: 0 10px 0 0;
}
.manager-img:hover {
    cursor: pointer;
    opacity: 0.5;
}
.divider {
    height: 1px;
    background: #e8e8ed;
    margin: 10px 0;
}
.community-label {
    display: inline-block;
    padding: 0 8px;
    height: 24px;
    background: #f0f0f2;
    border-radius: 6px;
    line-height: 24px;
    font-size: 14px;
    font-family: PingFangSC-Regular,PingFang SC;
    font-weight: 400;
    color: #555666;
    margin: 8px 8px 8px 0;
}
.el-icon-s-custom {
    padding: 14px;
    background-color: #E1F3D8;
    color: #67C23A;
    border-radius: 50%;
    font-size: 20px;
}
.el-icon-s-home {
    padding: 14px;
    background-color: #FAECD8;
    color: #E6A23C;
    border-radius: 50%;
    font-size: 20px;
}
.el-icon-s-data {
    padding: 14px;
    color: #F56C6C;
    background-color: #FDE2E2;
    border-radius: 50%;
    font-size: 20px;
}
.publish-article:hover {
    opacity: 0.5;
    cursor: pointer;
}
.el-icon-s-promotion {
    padding: 14px;
    background-color: #D9ECFF;
    color: #53A8FF;
    border-radius: 50%;
    font-size: 20px;
}

.community-function {
    margin-top: 16px;
    font-size: 12px;
    padding: 10px;
    background-color: #F5F6F7;
    text-align: center;
}
.community-member-content {
    font-size: 14px;
    color: gray;
}
.community-content-count {
    padding: 10px;
    background-color: #F5F6F7;
}
.community-content-count:hover {
    cursor: pointer;
    opacity: 0.5;
}
.community-member-count {
    padding: 10px;
    background-color: #F5F6F7;
}
.community-member-count:hover {
    cursor: pointer;
    opacity: 0.5;
}
.community-name {
    vertical-align: middle;
    font-size: 14px;
    text-align: left;
    font-weight: bold;
    max-width: 180px;
    display: inline-block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    
}
.community-img {
    width: 40px;
    height: 40px;
    vertical-align: middle;
    margin-right: 10px;
}
.community-img:hover {
    cursor: pointer;
    opacity: 0.5;
}
.header {
    padding: 16px; 
    border: 1px solid rgba(0, 0, 0, 0.1);
}
.MonkeyWebCommunityInfo-container {
    background-color: #fff;
    min-width: 100%; 
    max-width: 100%;
    height: 85vh;
    overflow-y: auto;
}

</style>