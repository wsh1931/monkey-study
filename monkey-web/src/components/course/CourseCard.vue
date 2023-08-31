<template>
    <div class="MonkeyWebCourseCard-container" style=" background-color: #FFFFFF;">
        <el-row>
                <el-row class="course-row " style="position: relative; padding: 10px;">
                    <div @click="toCourseDetail(course.id)" class="hover show-up even background margin-top2" v-for="course in courseList" :key="course.id">
                        <el-row :gutter="20">
                            <el-col :span="9">
                                <div style="overflow: hidden; height: 132px;">
                                    <img style="height: 132px; width: 242px;" :src="course.picture" alt="">
                                    <div class="formType">{{ course.courseFormType }}</div>
                                </div>
                            </el-col>
                            <el-col :span="15">
                                <el-row style="font-weight: 600;" class="ellipsis">
                                    {{ course.title }}
                                </el-row>
                                <el-row class="teacher-introduce ellipsis-more-row ">
                                    {{ course.userProfile }}
                                </el-row>
                                <el-row style="margin-top: 20px;">
                                    <el-col :span="8">
                                        <span v-if="course.isFree == '1' && course.formTypeId == '3'" class="first-letter"> {{ course.price }}</span>
                                        <span v-else class="course-free"> {{ course.courseFormType }}</span>
                                    </el-col>
                                    <el-col :span="8">
                                            <span class="el-icon-notebook-1" style="font-size: 14px;">共 {{ course.sectionCount }} 节</span>
                                        </el-col>
                                        <el-col :span="8">
                                            <span class="el-icon-user" style="font-size: 14px;"> {{ course.studySum }} 人学习</span>
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
    props: ['courseList'],
    data() {
        return {
            // 0表示默认排序，1表示升序排序，-1表示降序排序
            priceType: 0,
        };
    },

    methods: {
        // // 跳到课程详情页面
        // toCourseDetail(courseId) {
        //     this.$router.push({
        //         name: "course_detail",
        //         params: {
        //             courseId
        //         }
        //     })
        // }
            toCourseDetail(courseId) {
            const { href }  = this.$router.resolve({
                name: "course_detail",
                params: {
                    courseId
                },
            });
            window.open(href, '_blank')
        },

    },
};
</script>

<style scoped>
.margin-top2:nth-child(n + 3) {
    margin-top: 10px;
}
.background {
    transition: 0.4s linear all;
}
.background:hover {
background-image: linear-gradient(to top, #d5d4d0 0%, #d5d4d0 1%, #eeeeec 31%, #efeeec 75%, #e9e9e7 100%);
}
.first-letter {
    color: red; 
    font-size: 14px;
}
.course-free {
    color: red; 
    font-size: 14px; 
    margin-bottom: 3px;
}
.ellipsis {
  display: block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-align: left;
}
.formType {
    position: absolute;
    margin: 10px;
    top: 0;
    width: 10%;
    height: 15%;
    text-align: center;
    color: white;
    font-size: 10px;
    background-color: #18A4E1
}
.first-letter::before {
    content: "￥";
}
div.even:nth-child(2n) {
    margin-left: 15px;
}
.course-row {
    display: flex; 
    float:left; 
    flex-wrap: wrap; 
    padding: 10px;
    overflow: hidden;
}

.show-up {
    width: 610px;
    height: 132px;
    padding: 10px;
    animation: show-up 0.4s linear;
}

@keyframes show-up {
    0% {
        transform: translateY(100px);
        opacity: 0;
    }
    60% {
        transform: translateY(30px); 
        opacity: 1;
    }
    80% {
        transform: translateY(-10px);
    }
    100% {
        transform: translateY(0);
    }
}
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
img {
    transition: 0.2s linear all;
}
.hover:hover {
    cursor: pointer;
    box-shadow: 0 0 20px 8px #409EFF;
    position: relative;
    top: -2px;
}
img {
    background-size: cover;
}
.hover:hover img {
    transform: scale(1.5);
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