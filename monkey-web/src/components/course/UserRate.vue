<template>
    <div class="MonkeyWebUserRate-container">
    <span class="user-care" v-for="courseEvaluate in courseEvaluateList" :key="courseEvaluate.id">
        <el-row>
            <el-col :span="2">
                <img @click="toUserView(courseEvaluate.userId)" class="user-img" :src="courseEvaluate.headImage" alt="">
            </el-col>
            <el-col :span="10" class="username">
                {{ courseEvaluate.username }}
            </el-col>
            <el-col :span="12">
                <el-rate
                :show-score="false"
                v-model="courseEvaluate.courseEvaluate"
                disabled
                text-color="#ff9900"
                >
                </el-rate>
            </el-col>
        </el-row>
        <el-row >
            <el-col 
            :span="7" 
            class="label" 
            style="text-align: center;" 
            v-for="label in courseEvaluate.labelList"
            :key="label.id">{{ label }}</el-col>
        </el-row>
        <el-row class="evaluate-content ellipsis-more-row">
            {{ courseEvaluate.commentContent }}
        </el-row>
    </span>
    </div>
</template>

<script>
import $ from "jquery"
export default {
    name: 'MonkeyWebUserRate',
    props: ['currentPage', 'pageSize'],
    data() {
        return {
            value: 1.2,
            // 课程播放url
            coursePlayUrl: "http://localhost/monkey-course/video/player",
            // 课程评价用户列表
            courseEvaluateList: [],
        };
    },
    watch: {
        currentPage(newValue, oldValue) {
            console.log(newValue);
            this.getCourseScoreUserList(this.courseId)
        }
    },

    created() {
        this.courseId = this.$route.params.courseId;
        this.getCourseScoreUserList(this.courseId);
    },

    methods: {
        // 跳转至用户主页
        toUserView(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, '_black');
        },
        // 得到评价用户集合
        getCourseScoreUserList(courseId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/getCourseScoreUserList",
                type: "get",
                data: {
                    courseId,
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize
                },
                success(response) {
                    if (response.code == '200') {
                        vue.courseEvaluateList = response.data.records;
                        vue.$emit('updateTotal', response.data.total);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        }
    },
};
</script>

<style scoped>

.ellipsis-more-row {
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
  /* 设置省略行 */
    -webkit-line-clamp: 2; 
    -webkit-box-orient: vertical;  
}
.evaluate-content {
    font-size: 12px;
    padding-top: 10px;
}
.label:nth-child(n + 2) {
    margin-left: 10px;
}
.label {
    font-size: 12px;
    background-color: #FFFFFF;
    font-weight: bold;
    border-radius: 10px;
}
::v-deep .el-rate__icon{
    font-size: 18px
}
.username {
    font-size: 12px;
    padding-left: 5px;
}
.user-img {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    cursor: pointer;
}
.user-care:nth-child(2) {
    margin-left: 10px;
}
.user-care {
    display: inline-block;
    padding: 10px;
    border-radius: 10px;
    width: 45.3%;
    background-color: #DDE1E5;
    height: 10.5vh;
}

.MonkeyWebUserRate-container {
    border-radius: 10px;
}
</style>