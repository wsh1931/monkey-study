<template>
    <div class="MonkeyWebCoursePayFinishViews-container">
        <el-row class="one-area">
            <el-col :span="6" style="text-align: center;">
                <el-row class="title">订单完成</el-row>
                <el-row class="order-number">订单号：{{ orderInformation.id }}</el-row>
                <el-row>
                    <el-col :span="12" v-if="orderInformation.associationId != null && orderInformation.associationId != ''">
                        <el-button type="primary" size="mini" @click="toCourseDetailViews(orderInformation.associationId)">
                            立即观看
                        </el-button>
                        
                    </el-col>
                    <el-col :span="12" v-if="orderInformation.associationId != null && orderInformation.associationId != ''">
                        <el-button type="primary" size="mini" @click="toCourseEvaluateViews(orderInformation.id)">
                            评价商品
                        </el-button>
                    </el-col>
                </el-row>
            </el-col>
            <div class="divider"></div>
            <el-col :span="17">
                <el-steps :active="3" align-center>
                    <el-step title="提交订单" description="2023-08-29 16:37:21"></el-step>
                    <el-step title="正在处理" description="2023-08-29 16:37:21"></el-step>
                    <el-step title="订单完成" description="2023-08-29 16:37:21"></el-step>
                </el-steps>
            </el-col>
        </el-row>

        <el-row class="order-description">
            <el-col :span="12">
                商品信息
            </el-col>
            <el-col :span="4" style="text-align: center;">
                订单状态
            </el-col>
            <el-col :span="4" style="text-align: center;">
                支付方式
            </el-col>
            <el-col :span="4" style="text-align: center;">
                支付金额
            </el-col>
                    
        </el-row>
        <el-row class="two-area">
            <el-col :span="12">
                <el-row>
                    <el-col :span="5" style="overflow: hidden;">
                        <img class="order-img" 
                        width="100%" 
                        :src="orderInformation.picture" alt="" 
                        @click="toCourseDetailViews(orderInformation.associationId)">
                    </el-col>
                    <el-col :span="19" style="padding-left: 10px;">
                        <el-row class="course-title">
                            <div @click="toCourseDetailViews(orderInformation.associationId)">
                                {{ orderInformation.title }}
                            </div>
                        </el-row>
                        <el-row>{{ orderInformation.orderType }}</el-row>
                    </el-col>
                </el-row>
            </el-col>
            <el-col :span="4" style="text-align: center;">
                {{ orderInformation.orderStatus }}
            </el-col>
            <el-col :span="4" style="text-align: center;">
                {{ orderInformation.payWay }}
            </el-col>
            <el-col :span="4" style="text-align: center; color: red;">
                ￥ {{ orderInformation.orderMoney }}
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from "jquery"
import store from "@/store";
export default {
    name: 'MonkeyWebCoursePayFinishViews',

    data() {
        return {
            // 课程支付完成地址
            coursePayUrl: "http://localhost/monkey-course/pay/finish",
            // 订单信息
            orderInformation: {},
            // 订单id
            orderId: "",
        };
    },

    created() {
        this.orderId = this.$route.params.orderId;
        this.queryOrderInfoByOrderId(this.orderId)
    },

    methods: {
        // 前往课程评价页面
        toCourseEvaluateViews(orderId) {
            this.$router.push({
                name: "course_evaluate",
                params: {
                    orderId
                }
            })
        },
        // 前往课程详情页面
        toCourseDetailViews(courseId) {
            this.$router.push({
                name: "course_detail",
                params: {
                    courseId
                }
            })
        },
        // 通过订单id得到订单信息
        queryOrderInfoByOrderId(orderId) {
            const vue = this;
            $.ajax({
                url: vue.coursePayUrl + "/queryOrderInfoByOrderId",
                type: "get",
                data: {
                    orderId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.orderInformation = response.data;
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
.order-description {
    background-color: #F9F9F9; 
    margin-top: 20px; 
    padding: 5px 20px;
    opacity: 0.5;
}
.course-title {
    cursor: pointer;
    font-weight: 600;
    height: 82px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    /* 设置省略行 */
    -webkit-line-clamp: 4; 
    -webkit-box-orient: vertical; 
}
.course-title:hover {
    color: #409EFF;
}
.order-img {
    cursor: pointer;
    transition: 0.2s linear all;
    height: 100px;
    width: 100%;
}

.order-img:hover {
    transform: scale(1.4);
}

.two-area {
    padding: 20px;
    background-color: #FFFFFF;
}
.one-area {
    position: relative;  
    background-color: #FFFFFF; 
    padding: 20px;
}
.divider {
    width: 1px;
    background-color: gray;
    height: 70%;
    display: inline-block;
    position: absolute;
    opacity: 0.5;
}
.order-number {
    color: gray;
    padding: 10px 0;
}
.title {
    color: #409EFF;
    font-size: 24px;
    font-weight: 600;
}
.MonkeyWebCoursePayFinishViews-container {
    width: 1200px;
    margin: 100px auto;
}

</style>