<template>
    <div class="MonkeyWebCoursePayFinishViews-container">
        <el-row class="one-area">
            <el-col :span="6" style="text-align: center;">
                <el-row class="title">订单完成</el-row>
                <el-row class="order-number">订单号：{{ orderInformation.id }}</el-row>
                <el-row>
                    <el-col :span="12" v-if="orderInformation.orderType == '课程订单'">
                        <el-button type="primary" size="mini" @click="toCourseDetailViews(orderInformation.associationId)">
                            立即观看
                        </el-button>
                    </el-col>
                    <el-col :span="12" v-if="orderInformation.orderType == '资源订单'">
                        <el-button type="primary" size="mini" @click="toCourseDetailViews(orderInformation.associationId)">
                            前往下载
                        </el-button>
                    </el-col>
                    <el-col :span="12" v-if="orderInformation.orderType == '课程订单'">
                        <el-button type="primary" size="mini" @click="toCourseEvaluateViews(orderInformation.id)">
                            评价商品
                        </el-button>
                    </el-col>
                    <el-col :span="12" v-if="orderInformation.orderType == '资源订单'">
                        <el-button type="primary" size="mini" @click="queryUserResourceScore(orderInformation.associationId)">
                            评价资源
                        </el-button>
                    </el-col>
                </el-row>
                <!-- 评价资源对话框 -->
                <el-dialog
                :title="'评价' + orderInformation.title"
                :visible.sync="showEvaluateResourceDialog"
                width="30%"
                top="200px"
                center>
                <span class="resource-score">资源评分</span>
                <el-rate
                style="display: inline-block;"
                    v-model="resourceScore"
                    :texts=texts
                    show-text
                    :colors="colors"
                    text-color="#FC5531">
                </el-rate>
                <span slot="footer" class="dialog-footer">
                    <el-button @click="showEvaluateResourceDialog = false">取 消</el-button>
                    <el-button type="primary" @click="submitResourceScore(orderInformation.associationId, resourceScore)">确 定</el-button>
                </span>
                </el-dialog>
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
                        v-if="orderInformation.orderType == '课程订单'"
                        :src="orderInformation.picture" alt="" 
                        @click="toCourseDetailViews(orderInformation.associationId)">

                        <img class="order-img" 
                        width="100%" 
                        v-if="orderInformation.orderType == '资源订单'"
                        :src="orderInformation.picture" alt="" 
                        @click="toResourceDetailViews(orderInformation.associationId)">
                    </el-col>
                    <el-col :span="19" style="padding-left: 10px;">
                        <el-row class="course-title">
                            <div  
                            v-if="orderInformation.orderType == '课程订单'" 
                            @click="toCourseDetailViews(orderInformation.associationId)">
                                {{ orderInformation.title }}
                            </div>

                            <div  
                            v-if="orderInformation.orderType == '资源订单'" 
                            @click="toResourceDetailViews(orderInformation.associationId)">
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
            resourceScore: "",
            // 评分颜色
            colors: ["grey", "orange", "red"],
            // 评分描述内容
            texts: ['不推荐', '一般推荐', '推荐', '力荐', '特别满意'],
            // 课程支付完成地址
            orderPayFinish: "http://localhost/monkey-user/pay/finish",
            resourcePayFinishUrl: "http://localhost/monkey-resource/pay/finish",
            // 订单信息
            orderInformation: {},
            // 订单id
            orderId: "",
            // 是否展示评价资源对话框
            showEvaluateResourceDialog: false,
        };
    },

    created() {
        this.orderId = this.$route.params.orderId;
        this.queryOrderInfoByOrderId(this.orderId)
    },

    methods: {
        // 查询用户资源评分
        queryUserResourceScore(resourceId) {
            const vue = this;
            $.ajax({
                url: vue.resourcePayFinishUrl + "/queryUserResourceScore",
                type: "get",
                data: {
                    resourceId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.showEvaluateResourceDialog = true;
                        vue.resourceScore = response.data;
                    } else {
                        vue.$modal.msgError(response, msg);
                    }
                }
            })
        },
        // 提交资源评分
        submitResourceScore(resourceId, resourceScore) {
            const vue = this;
            $.ajax({
                url: vue.resourcePayFinishUrl + "/submitResourceScore",
                type: "post",
                data: {
                    resourceId,
                    resourceScore
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.showEvaluateResourceDialog = false;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.$modal.msgError(response, msg);
                    }
                }
            })
        },
        // 前往课程评价页面
        toCourseEvaluateViews(orderId) {
            this.$router.push({
                name: "course_evaluate",
                params: {
                    orderId
                }
            })
        },
        // 前往资源详情页面
        toResourceDetailViews(resourceId) {
            this.$router.push({
                name: "resource_detail",
                params: {
                    resourceId
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
                url: vue.orderPayFinish + "/queryOrderInfoByOrderId",
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
.resource-score {
    margin-right: 10px;
    vertical-align: middle;
}
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