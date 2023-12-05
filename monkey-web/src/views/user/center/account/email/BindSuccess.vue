<template>
    <div class="MonkeyWebBindSuccess-container">
        <div @click="toUserAccountViews()" class="return">点击返回账号设置</div>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebBindSuccess',

    data() {
        return {
            
        };
    },

    methods: {
        toUserAccountViews() {
            this.$router.push({
                name: "user_center_account"
            })
        }
    },
};
</script>

<style scoped>
.return:hover {
    opacity: 0.8;
}
.return {
    text-align: center;
    color: #409EFF;
    cursor: pointer;
}
.MonkeyWebBindSuccess-container {
    padding: 20px;
    background-color: #fff;
}
</style>