<template>
    <div class="MonkeyWebCourseCard-container" style=" background-color: #FFFFFF;">
        
        <el-row>
                <el-row style="display: flex; float: left;">
                    <div style="width: 635px; height: 132px; padding: 10px; " class="hover">
                        <el-row >
                            <el-col :span="10">
                                <img style="height: 132px; width: 242px;" src="https://cdn.acwing.com/media/user/profile/photo/153408_md_51dacd8c7e.webp" alt="">
                            </el-col>
                            <el-col :span="14">
                                <el-row>
                                    小程序开发入门
                                </el-row>
                                <el-row class="teacher-introduce ellipsis-more-row ">
                                    讲师介绍: 非常牛逼的全栈开发人员讲师介绍讲师介绍: 非常牛逼的全栈开发人员讲师介绍讲师介绍: 非常牛逼的全栈开发人员讲师介绍讲师介绍: 非常牛逼的全栈开发人员讲师介绍讲师介绍: 非常牛逼的全栈开发人员讲师介绍
                                </el-row>
                                <el-row style="margin-top: 20px;">
                                    <el-col :span="8">
                                        <span class="el-icon-wallet" style="color: red;">1008.00</span>
                                    </el-col>
                                    <el-col :span="8">
                                            <span class="el-icon-notebook-1">共154节</span>
                                        </el-col>
                                        <el-col :span="8">
                                            <span class="el-icon-user">19581人学习</span>
                                        </el-col>
                                     
                                </el-row>
                            </el-col>
                        </el-row>
                </div>
                
            </el-row>
    </el-row>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebCourseCard',

    data() {
        return {
            // 0表示默认排序，1表示升序排序，-1表示降序排序
            priceType: 0,
            // 搜索内容
            searchContent: "",
        };
    },

    methods: {
        
    },
};
</script>

<style scoped>
.ellipsis-more-row {

   height: 50px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  /* 设置省略行 */
  -webkit-line-clamp: 3; 
  -webkit-box-orient: vertical;

}
.teacher-introduce {
    margin-top: 20px;
    font-size: 10px;
    color: rgba(0, 0, 0, 0.5);
}
.hover:hover {
    cursor: pointer;
    box-shadow: 0 0 20px 10px #EEEEEE;
    background-color: #F7F7FC;

}
  .text {
    font-size: 14px;
  }

  .item {
    padding: 18px 0;
  }

  .box-card {
    width: 550px;
  }
</style>