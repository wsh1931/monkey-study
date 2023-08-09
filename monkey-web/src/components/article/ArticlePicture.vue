<template>
    <div class="MonkeyWebArticlePicture-container">
        <img class="right-slide" :src="article.photo" alt=""  @click="toCheckArticle(article.id)">
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
            const { href } = this.$router.resolve({
                name: "check_article",
                params: {
                    articleId
                }
            })

            window.open(href, '_black')
        }
    },
};
</script>

<style scoped>
.MonkeyWebArticlePicture-container {
    overflow: hidden;
}
.right-slide:hover {
    cursor: pointer;
    transform: scale(1.2);
    transition: 0.4s linear all;
}

.right-slide {
    width: 350px; 
    height: 193px; 
    animation: right-slide 0.5s linear;
}
@keyframes right-slide {
    0% {
        opacity: 0;
        transform: translateX(100px);
    }
    100% {
        opacity: 1;
        transform: translateX(0);
    }
}
</style>