<template>
    <div class="CarouseMap-container" style="margin: 0 auto;">
                <el-carousel :loop="true" trigger="click" :interval="4000" height="391px" class="carousel show-in" indicator-position="none">
                    <el-carousel-item v-for="article in articleSortList" :key="article.id">
                        <div @click="toCheckArticle(article.id)" class="carousel_item">
                            <img :src="article.photo" alt="">
                            <span  class="characters-in-plcture">{{ article.title }}</span>
                        </div>
                    </el-carousel-item>
                </el-carousel>
    </div>
</template>

<script>
 export default {
    name: "CarouseMap",
    props: ['articleSortList'],
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
    }
 }
</script>

<style scoped>
.CarouseMap-container {
  overflow: hidden;
}
.show-in {
 animation: show-in 0.7s linear;
}

@keyframes show-in {
  0% {
    opacity: 0;
    transform: translateX(-100px);
  }
  100% {
    opacity: 1;
    transform: translateX(0);
  }
}

  .carousel:hover {
    cursor: pointer;
    transform: scale(1.01);
    transition: 0.2s linear all;
  }
  .el-carousel__item h3 {
    color: #475669;
    font-size: 14px;
    opacity: 0.75;
    line-height: 200px;
    margin: 0;
  }
  
  .el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
  }
  
  .el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
  }

  .carousel_item {
  position: relative;
  height: 100%;
}

.carousel_item img {
  width: 100%;
  height: 100%;
}

.characters-in-plcture {
  position: absolute;
  left: 0px;
  bottom: 5vh;
  text-align: center;
  opacity: 0.5;
  width: 100%;
  color: white;
  background-color: #2E0B12;
}
  /* .carousel_item span {
    position: absolute;
    left: 0rem;
    bottom: 2rem;
  }

  .characters-in-plcture {
    background-color: rgba(0, 0, 0, 0.5);
    width: 100%;
    text-align: center;
  } */
</style>