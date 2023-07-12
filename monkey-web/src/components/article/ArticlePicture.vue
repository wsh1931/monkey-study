<template>
    <div class="MonkeyWebArticlePicture-container">
        <img :src="article.photo" alt="" style="width: 350px; height: 193px; cursor: pointer;" @click="toCheckArticle(article.id)">
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebArticlePicture',
    props: ["article"],
    data() {
        return {
            
        };
    },

    methods: {
        // 跳往查看文章界面
        toCheckArticle(articleId) {
            this.$router.push({
                name: "check_article",
                params: {
                    articleId
                }
            })
        }
    },
};
</script>

<style lang="scss" scoped>

</style>