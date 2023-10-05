<template>
    <div>
        <el-empty 
        image="https://monkey-blog.oss-cn-beijing.aliyuncs.com/error/notFound.jpg" 
        class="notFound"
        description="您要找的数据可能已经被删除"
        :image-size="500" >
        <el-button  type="primary" @click="backView()">返回原地址</el-button>
        </el-empty>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebNotFoundViews',
    methods: {
        backView() {
            this.$router.go(-1)
        }
    },
};
</script>

<style scoped>
.MonkeyWebNotAuthorizationViews-container {
    position: relative;
}
.notFound {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}
</style>