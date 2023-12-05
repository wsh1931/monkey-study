<template>
    <div class="MonkeyWebUserCenter-container">
        <el-row>
            <el-col :span="4">
                <el-menu
                router
                :default-active="defaultRouter"
                class="el-menu-vertical-demo"
                @open="handleOpen"
                text-color="black"
                active-text-color="#409EFF"
                @close="handleClose">
                <el-menu-item index="/user/center/profile">
                    <i class="el-icon-user"></i>
                    <span slot="title">个人资料</span>
                </el-menu-item>
                <el-menu-item index="/user/center/account">
                    <i class="el-icon-setting"></i>
                    <span slot="title">账号设置</span>
                </el-menu-item>
                <el-menu-item index="/user/center/collect">
                    <i class="el-icon-collection"></i>
                    <span slot="title">我的收藏</span>
                </el-menu-item>
                <el-menu-item index="/user/center/history">
                    <i class="el-icon-time"></i>
                    <span slot="title">游览历史</span>
                </el-menu-item>
                <el-menu-item>
                    <i class="el-icon-time"></i>
                    <span slot="title">实名认证</span>
                </el-menu-item>
                <el-menu-item @click="toCreateCenterViews()">
                    <span slot="title" style="position: relative;">
                        <span>创作中心</span>
                        <i class="el-icon-arrow-right"></i>
                    </span>
                </el-menu-item>

                <!-- todo 前往内容管理 -->
                <el-menu-item @click="toContentManage()">
                    <span slot="title" style="position: relative;">
                        <span>内容管理</span>
                        <i class="el-icon-arrow-right"></i>
                    </span>
                </el-menu-item>

                <!-- todo -->
                <el-menu-item @click="toContentManage()">
                    <span slot="title" style="position: relative;">
                        <span>我的钱包</span>
                        <i class="el-icon-arrow-right"></i>
                    </span>
                </el-menu-item>

                <!-- todo -->
                <el-menu-item @click="toContentManage()">
                    <span slot="title" style="position: relative;">
                        <span>直播中心</span>
                        <i class="el-icon-arrow-right"></i>
                    </span>
                </el-menu-item>
                </el-menu>
            </el-col>
            <el-col :span="20">
                <router-view></router-view>
            </el-col>
        </el-row>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebUserCenter',

    data() {
        return {
            defaultRouter: "",
        };
    },

    created() {
        this.defaultRouter = this.$route.fullPath;
        this.routerName = this.$route.name;
        if (this.routerName == 'email_verify' || this.routerName == 'email_verify' || this.routerName == 'email_success') {
            this.defaultRouter = "/user/center/account";
        }
    },

    methods: {
        // todo 前往创作中心
        toCreateCenterViews() {
            
        },
        handleOpen(key, keyPath) {
            console.log(key, keyPath);
        },
        handleClose(key, keyPath) {
            console.log(key, keyPath);
        }
    },
};
</script>

<style scoped>
.el-icon-arrow-right {
    position: absolute;
    right: -50px;
    top: 0;
}
::v-deep .el-menu-vertical-demo {
    text-align: center;
    font-size: 16px;
    height: calc(100vh - 81px);
}
.MonkeyWebUserCenter-container {
    width: 1000px;
    margin: 0 auto;
    margin-top: 20px;
}

</style>