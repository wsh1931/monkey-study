<template>
    <div class="MonkeyWebCourseBuyViews-container">
        <el-steps :active="1" align-center finish-status="process">
            <el-step title="确认订单信息" description=""></el-step>
            <el-step title="提交订单" description=""></el-step>
            <el-step title="订单完成" description=""></el-step>
            <el-step title="商品评价" description=""></el-step>
        </el-steps>

        <el-row style="margin-top: 30px;">
            <el-row class="course-info-confirm">课程信息确认</el-row>
            <el-row style="margin-top: 10px;">
                <el-col :span="7">
                    <img class="course-img" :src="courseInfo.picture" alt="">
                </el-col>
                <el-col :span="17" style="padding-left: 10px;">
                    <el-row class="course-title">{{ courseInfo.title }}</el-row>
                    <el-row class="course-profile">
                        {{ courseInfo.introduce }}
                    </el-row>
                    <el-row style="margin-top: 10px;">
                        <el-col :span="8">
                            <span class="first-letter"> {{ courseInfo.coursePrice }}</span>
                        </el-col>
                        <el-col :span="8">
                                <span class="el-icon-notebook-1" style="font-size: 14px;">共&nbsp;{{ courseInfo.sectionCount }}&nbsp;节</span>
                        </el-col>
                        <el-col :span="8">
                            <span class="el-icon-user" style="font-size: 14px;">&nbsp;{{ courseInfo.studyCount }}&nbsp;人学习</span>
                        </el-col>
                    </el-row>
                </el-col>
            </el-row>
        </el-row>


        <!-- 课程提醒 -->
        <el-row style="padding-top: 20px;">
            课程提醒发送至
        </el-row>
        <el-row style="padding: 20px 0;" v-if="courseInfo.hasEmail == '1'">
            QQ邮箱：{{ courseInfo.email }}
        </el-row>
        <el-row v-else>
            <el-button type="text" @click="toUserViews($store.state.user.id)">去个人主页绑定QQ邮箱</el-button>
        </el-row>
        <el-row class="divisor">

        </el-row>
        <el-row>
            <el-col :span="2" class="charge-way">支付方式</el-col>
            <el-col :span="3" :class="[{isSelectChargeWay: isSelectChargeWay == '2'}]">
                <div class="iconfont icon-weixin wx-charge" @click="isSelectChargeWay = 2"> 支付宝支付</div>
                
            </el-col>
        </el-row>

        <el-row style="padding: 10px 0;">需支付金额</el-row>
        <el-row class="monkey">
            <el-col :span="2">
                课程价格
            </el-col>
            <el-col :span="20" style="color: red; text-decoration: line-through;">
                ${{ courseInfo.coursePrice }}
            </el-col>
        </el-row>
        <el-row class="monkey">  
            <el-col :span="2">
                应付金额
            </el-col>
            <el-col :span="20" style="color: red;">
                ${{ courseInfo.discountPrice }}
            </el-col>
        </el-row>

        <el-row style="text-align: right;">
            <el-button type="primary" class="submit-button" @click="submitOrder(courseInfo.id)">提交订单</el-button>
        </el-row>
        <div>{{ page }}</div>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebCourseBuyViews',

    data() {
        return {
            active: 1,
            isFree: 0,
            // 用户是否绑定邮箱
            userBingEmail: false,
            // 0表示微信支付, -1表示还没选
            isSelectChargeWay: -1,
            // 课程详细信息地址
            coursePayUrl: "http://localhost/monkey-course/pay",
            // 课程id
            courseId: "",
            // 课程信息
            courseInfo: {},
            page: "",
        };
    },

    created() {
        this.courseId = this.$route.params.courseId;
        this.getCourseInfoByCourseId(this.courseId);
    },

    methods: {
        // 提交课程订单
        submitOrder(courseId) {
            if (this.isSelectChargeWay == '-1') {
                this.$modal.msgWarning("请选择支付方式");
                return false;
            }
            const vue = this;
            $.ajax({
                url: vue.coursePayUrl + "/tradePagePay",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    courseId,
                    isSelectChargeWay: vue.isSelectChargeWay
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        document.write(response.data);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 跳转至用户主页
        toUserViews(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, "_black");
        },
        getCourseInfoByCourseId(courseId) {
            const vue = this;
            $.ajax({
                url: vue.coursePayUrl + "/getCourseInfoByCourseId",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    courseId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.courseInfo = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                },
            })
        }
    },
};
</script>

<style scoped>
.submit-button {
    border-radius: 20px;
}
.monkey {
    padding: 10px 0;
    font-size: 14px;
}
.divisor {
    height: 1px;
    opacity: 0.2;
    background-color: black;
    margin: 20px 0px;
}
.charge-way {
    font-size: 14px;
    padding: 5px 0;
}
.isSelectChargeWay {
    background-color: #409EFF;
    color: white;
}
.wx-charge:hover {
    background-color: #409EFF;
    opacity: 0.5;
    color: white;
}
.wx-charge {
    cursor: pointer;
    text-align: center;
    padding: 5px 0px;
}
.first-letter::before {
    content: "￥";
}
.course-free {
    color: red; 
    font-size: 14px; 
    margin-bottom: 3px;
}
.first-letter {
    color: red; 
    font-size: 14px;
}
.course-title {
    font-weight: 600;
}
.course-profile {
    padding: 20px 0 0 0; 
    font-size: 14px;
    height: 15vh;
    opacity: 0.5;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    /* 设置省略行 */
    -webkit-line-clamp: 5; 
    -webkit-box-orient: vertical;  
}
.course-info-confirm {
    font-weight: 600;
}
.course-img {
    width: 20vw;
    height: 12vw;
}
.MonkeyWebCourseBuyViews-container {
    background-color: #FFFFFF;
    width: 1000px;
    margin: 20px auto;
    padding: 20px;
}
</style>