<template>
    <div class="MonkeyWebNotAuthorizationViews-container">
        <el-empty 
        :image-size="500" 
        class="notAuthorization" 
        description=" "
        image="https://monkey-blog.oss-cn-beijing.aliyuncs.com/error/403.jpg">
            
            <el-button @click="backView()" type="primary">前往社区首页</el-button>
        </el-empty>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebNotAuthorizationViews',
    methods: {
        backView() {
            this.$router.push({
                name: "community",
            })
        }
    },
};
</script>

<style scoped>
.MonkeyWebNotAuthorizationViews-container {
    position: relative;
}
.notAuthorization {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}
</style>