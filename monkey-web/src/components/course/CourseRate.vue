<template>
    <div class="MonkeyWebCourseRate-container">
        <el-row>
            <el-col :span="10">
                <el-row style="text-align: center;">课程评分</el-row>
                <el-row style="padding: 0px 0px 10px 0px;">
                    <el-col :span="6">
                        <span class="iconfont icon-chibangzuo wing"></span>
                    </el-col>
                    <el-col :span="12" class="score">
                        {{ courseInfo.score * 2 }}
                    </el-col>
                    <el-col :span="6">
                        <span class="iconfont icon-chibangyou wing"></span>
                    </el-col>
                </el-row>

                <el-row style="text-align: center;">{{ courseInfo.evaluateCount }}人点评</el-row>
            </el-col>
            <el-col :span="14">
                <span style="position: relative;">
                <span class="evaluate-title">特别满意</span>
                <el-progress 
                class="progress" 
                :text-inside="true"  
                :show-text="false" 
                :percentage="courseEvaluate.extremeRecommend / courseInfo.evaluateCount * 100"
                color="grey" ></el-progress>
                </span>
                <span style="position: relative;">
                <span class="evaluate-title-two">力荐</span>
                <el-progress 
                class="progress" 
                :text-inside="true"  
                :show-text="false" 
                :percentage="courseEvaluate.pushRecommend / courseInfo.evaluateCount * 100"
                color="grey" ></el-progress>
                </span>
                <span style="position: relative;">
                <span class="evaluate-title-two">推荐</span>
                <el-progress 
                class="progress" 
                :text-inside="true"  
                :show-text="false" 
                :percentage="courseEvaluate.recommend / courseInfo.evaluateCount * 100"
                color="grey" ></el-progress>

                </span >
                <span style="position: relative;">
                <span class="evaluate-title">一般推荐</span>
                <el-progress 
                class="progress" 
                :text-inside="true"  
                :show-text="false" 
                :percentage="courseEvaluate.mediumRecommend / courseInfo.evaluateCount * 100"
                color="grey" ></el-progress>
                </span>
                <span style="position: relative;">
                <span class="evaluate-title-three">不推荐</span>
                <el-progress 
                class="progress" 
                :text-inside="true"  
                :show-text="false" 
                :percentage="courseEvaluate.notRecommend / courseInfo.evaluateCount * 100"
                color="grey" ></el-progress>

                </span>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from "jquery"
export default {
    name: 'MonkeyWebCourseRate',
    props: ['courseInfo'],
    data() {
        return {
            // 课程id
            courseId: "",
            // 课程评价信息
            courseEvaluate: {},
            // 课程播放url
            coursePlayUrl: "http://localhost/monkey-course/video/player",
        };
    },

    created() {
        this.courseId = this.$route.params.courseId;
        this.getCourseScoreInfo(this.courseId);
    },

    methods: {
        // 得到课程评价信息
        getCourseScoreInfo(courseId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/getCourseScoreInfo",
                type: "get",
                data: {
                    courseId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.courseEvaluate = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
    },
};
</script>

<style scoped>
.evaluate-title-three {
    position: absolute;
    font-size: 12px;
    top: 5px;
    left: 18px;
}
.evaluate-title-two {
    position: absolute;
    font-size: 12px;
    top: 5px;
    left: 30px;
}
.evaluate-title {
    position: absolute;
    font-size: 12px;
    top: 5px;
    left: 6px;
}
.progress {
    padding: 10px 0px 0px 40%;
    width: 60%;
}
.wing {
    font-size: 25px;
}
.score {
    text-align: center;
    color: #FF7612;
    font-size: 20px;
    font-weight: 600;
}
.MonkeyWebCourseRate-container {
    border-radius: 10px;
    background-color: #DDE1E5;
    padding: 10px;
}
</style>