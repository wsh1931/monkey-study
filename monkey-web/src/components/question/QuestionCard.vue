<template>
    <div class="InterlocationCard-container">
        <el-card class="hover-border box-card show-question" v-for="question in questionList" :key="question.id">
            <el-row>
                <el-col :span="4">
                    <el-row>
                        <el-tag type="success">回答 {{ question.replyCount }}</el-tag>
                    </el-row>
                    <el-row style="margin-top: 30px;">
                        <el-tag type="warning">阅读 {{ question.visit }}</el-tag>
                    </el-row>
                </el-col>
                <el-col :span="20">
                    <el-row>
                        <el-col :span="20">{{ question.title }}</el-col>
                       
                        <el-col :span="4">
                            <el-button icon="el-icon-thumb" @click="toQuestionReply(question.id)" type="primary" size="medium" round>回答</el-button>
                        </el-col>
                    </el-row>
                    <el-row style=" width: 80%; margin-top: 20px; text-align: left;">
                        <el-col :span="9">
                            <div @click="toUserCenterHome(question.userId)">
                                <el-row >
                                    <el-col :span="9">
                                        <img class="hover" :src="question.userphoto" alt="">
                                    </el-col>
                                    <el-col :span="15">
                                        <div class="information" style="margin-top: 10px;">{{ question.username }}</div>
                                    </el-col>
                                </el-row>
                            </div> 
                            
                        </el-col>
                        
                        <el-col :span="5" class="information">
                            <span class="iconfont icon-dianzan"></span>
                            点赞 {{ question.userLikeCount }}
                        </el-col>
                        <el-col :span="4" class="information">
                            <span class="iconfont icon-shoucang"></span>
                            <span>收藏 {{ question.userCollectCount }}</span>
                        </el-col>
                        <el-col :span="6" style="margin-top: 10px; text-align: center;" class="information"><span class="el-icon-time "></span> {{ question.updateTime | formatDate }}</el-col>
                    </el-row>

                </el-col>
            </el-row>
        </el-card> 
    </div>
</template>

<script>
import $ from 'jquery'
 export default {
    name: "InterlocationCard",
    props: {
        questionList: Array
    },
    data() {
        return {
            questionUrl: "http://localhost:80/monkey-question",
        }
    },
    filters: {
        formatDate: value => {
        if (!value) return '';

        // 转换成 Date 对象
        const date = new Date(value);

        // 格式化输出
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);

        return `${year}-${month}-${day}`;
        }
    },
    methods: {
        // 跳转至问答回复界面
        toQuestionReply(questionId) {
             // 问答游览数 + 1
            const vue = this;
            $.ajax({
                url: vue.questionUrl + "/questionViewCountAddOne",
                type: "get",
                data: {
                    questionId
                },
                success(response) {
                    if (response.code != '200') {
                        vue.$modal.msgError(response.msg);
                    } else {
                        vue.$router.push({
                            name: "question_reply",
                            params: {
                                questionId
                            }
                        })
                    }
                },
                error(response) {
                    vue.$modal.msgError(response.msg);
                }
            })
        },
        // 点击用户头像跳转至用户主页
        toUserCenterHome(userId) {
            this.$router.push({
                name: "user_home",
                params: {
                    userId
                }
            })
        }
    }
 }
</script>

<style scoped>
.show-question {
    animation: show-question 0.6s linear;
}

@keyframes show-question {
    0% {
        opacity: 0;
        transform: translateY(-100px);
    }
    60% {
        opacity: 1;
        transform: translateY(30px);
    }
    80% {
        transform: -10px;
    }
    100% {
        opacity: translateX(0);
    }
}

.ellipsis {
    color: gray;
  display: block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-align: left;
}

.box-card {
    border-radius: 20px;
  transition: 0.5s linear all;
    border-radius: 2px;
    margin-bottom: 20px;
    border: 1px solid #dcdfe6;
    -webkit-box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    
    }

.hover-border:hover {
    box-shadow: 0 0 10px 5px #409EFF;
    position: relative;
    top: -1px;
    background-image: linear-gradient(to top, #d5d4d0 0%, #d5d4d0 1%, #eeeeec 31%, #efeeec 75%, #e9e9e7 100%);
}
.hover:hover {
    cursor: pointer;
    color: #46A1FF;
}
.hover {
    width: 40px; 
    height: 40px; 
    border-radius: 50%;
}

.information {
    margin-top: 12px; 
    color: rgba(0, 0, 0, 0.5);
     font-size: 14px;
}
</style>