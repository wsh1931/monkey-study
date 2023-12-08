<template>
    <div class="MonkeyWebAccountEmail-container">
        <el-steps :active="active" align-center class="navigate">
            <el-step title="验证身份"></el-step>
            <el-step title="绑定邮箱"></el-step>
            <el-step title="绑定成功"></el-step>
        </el-steps>
        <router-view class="child-class"></router-view>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebAccountEmail',
    data() {
        return {
            active: 1,
        };
    },
    watch: {
        "$route.name"(val) {
            let routerName = val
            if (routerName == "email_verify") {
                this.active = 1;
            } else if (routerName == "email_bind") {
                this.active = 2;
            } else if (routerName == "email_success") {
                this.active = 3;
            }
        }
    },
    created() {
        let routerName = this.$route.name;
        if (routerName == "email_verify") {
            this.active = 1;
        } else if (routerName == "email_bind") {
            this.active = 2;
        } else if (routerName == "email_success") {
            this.active = 3;
        }
    },

    methods: {
        
    },
};
</script>

<style scoped>
.navigate {
    text-align: center; 
    margin-bottom: 20px;
}
.MonkeyWebAccountEmail-container {
    padding: 20px;
    background-color: #fff;
    animation: slide-out 0.4s linear;
}
@keyframes slide-out {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.child-class {
    animation: slide-out 0.4s linear;
}
</style>