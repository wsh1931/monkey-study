<template>
    <div class="InterlocationCard-container">
        <el-card class="hover-border box-card" v-for="question in questionList" :key="question.id">
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
                                        <img class="hover" style="width: 40px; height: 40px; border-radius: 50%;" :src="question.userphoto" alt="">
                                    </el-col>
                                    <el-col :span="15">
                                        <div class="information" style="margin-top: 10px;">{{ question.username }}</div>
                                    </el-col>
                                </el-row>
                            </div>
                            
                        </el-col>
                        
                        <el-col :span="5" class="information el-icon-caret-top">
                            点赞 {{ question.userLikeCount }}
                        </el-col>
                        <el-col :span="4" class="information el-icon-collection">
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
 export default {
    name: "InterlocationCard",
    props: {
        questionList: Array
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
            this.$router.push({
                name: "question_reply",
                params: {
                    questionId
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
  
    border-radius: 2px;
    margin-bottom: 20px;
    border: 1px solid #dcdfe6;
    -webkit-box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    }
.hover-border:hover {
    box-shadow: 0 0 5px 5px lightblue;
}
.hover:hover {
    cursor: pointer;
    color: #46A1FF;
}

.information {
    margin-top: 12px; 
    color: rgba(0, 0, 0, 0.5);
     font-size: 14px;
}
</style>