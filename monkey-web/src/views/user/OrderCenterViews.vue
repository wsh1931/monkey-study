<template>
    <div class="MonkeyWebOrderCenterViews-container">
        <el-tabs type="border-card" class="el-tabs" v-model="tabName">
            <el-tab-pane name="all" :label="`全部订单 ${orderNumber.total == 0 ? '' : orderNumber.total}`" style="padding: 0px;"></el-tab-pane>
            <el-tab-pane name="unpaid" :label="`待付款 ${orderNumber.unpaid == 0 ? '' : orderNumber.unpaid}`"></el-tab-pane>
            <el-tab-pane name="waitEvaluate" :label="`待评价 ${orderNumber.waitEvaluate == 0 ? '' : orderNumber.waitEvaluate}`"></el-tab-pane>
            <el-tab-pane name="finished" :label="`已完成 ${orderNumber.finished == 0 ? '' : orderNumber.finished}`"></el-tab-pane>
            <el-tab-pane name="userCanceled" :label="`用户已取消 ${orderNumber.userCanceled == 0 ? '' : orderNumber.userCanceled}`"></el-tab-pane>
            <el-tab-pane name="exceedTimeAlreadyClose" :label="`超时已关闭 ${orderNumber.exceedTimeAlreadyClose == 0 ? '' : orderNumber.exceedTimeAlreadyClose}`"></el-tab-pane>
            <el-tab-pane name="refundSuccess" :label="`退款成功 ${orderNumber.refundSuccess == 0 ? '' : orderNumber.refundSuccess}`"></el-tab-pane>
            <el-tab-pane name="refundFail" :label="`退款失败 ${orderNumber.refundFail == 0 ? '' : orderNumber.refundFail}`"></el-tab-pane>
            <el-row style="padding: 0 20px;">
                <el-col :span="16">
                    订单详情
                </el-col>
                <el-col :span="2" style="text-align: center;">
                    支付方式
                </el-col>
                <el-col :span="2" style="text-align: center;">
                    支付金额
                </el-col>
                <el-col :span="2" style="text-align: center;">
                    支付状态
                </el-col>
                <el-col :span="2" style="text-align: center;">
                    操作
                </el-col>
            </el-row>

            <el-row class="divider"></el-row>

            <el-row style="margin-top: 20px; background-color: white;">
                <el-card class="el-card" v-for="(order, index) in orderList" :key="order.id">
                    <el-row>
                        <span class="order-time">{{ order.createTime }}</span>
                        <span class="order-number">订单号：{{ order.id }}</span>
                    </el-row>
                    <el-row style="padding: 20px 0;">
                    <el-col :span="16">
                        <el-row>
                            <el-col :span="5">
                                <div style="overflow: hidden;display: inline-block;">
                                    <img @click="toDetailViews(order)" class="class-photo" :src="order.picture" alt="">
                                </div>
                            </el-col>
                            <el-col :span="19">
                                <el-row class="course-title">
                                    <div @click="toDetailViews(order)">
                                        {{ order.title }}
                                    </div>
                                </el-row>
                                
                                <el-row class="pay-kind">
                                    {{ order.orderType }}
                                </el-row>
                            </el-col>
                        </el-row>
                        
                    </el-col>
                    <el-col :span="2" class="pay-way">
                        {{ order.payWay }}
                    </el-col>
                    <el-col :span="2" style="text-align: center;">
                        $ {{ order.orderMoney }}
                    </el-col>
                    <el-col :span="2" style="text-align: center;" v-if="order.orderStatus == '未支付'">
                        <el-tag type="warning">未付款</el-tag>
                    </el-col>
                    <el-col :span="2" style="text-align: center;" v-if="order.orderStatus == '待评价'">
                        <el-tag type="primary">待评价</el-tag>
                    </el-col>
                    <el-col :span="2" style="text-align: center;" v-if="order.orderStatus == '已完成'">
                        <el-tag type="success">已完成</el-tag>
                    </el-col>
                    <el-col :span="2" style="text-align: center;" v-if="order.orderStatus == '用户已取消'">
                        <el-tag type="warning">用户已取消</el-tag>
                    </el-col>
                    <el-col :span="2" style="text-align: center;" v-if="order.orderStatus == '超时已关闭'">
                        <el-tag type="danger">超时已关闭</el-tag>
                    </el-col>
                    <el-col :span="2" style="text-align: center;" v-if="order.orderStatus == '退款成功'">
                        <el-tag type="success">退款成功</el-tag>
                    </el-col>
                    <el-col :span="2" style="text-align: center;" v-if="order.orderStatus == '退款失败'">
                        <el-tag type="error">退款失败</el-tag>
                    </el-col>
                    <el-col :span="2" style="text-align: center;">
                        <el-row v-if="order.orderStatus == '未支付'">
                            <el-button @click="cancelOrder(order)" type="warning" size="mini" plain>取消订单</el-button>
                        </el-row>
                        <el-row v-if="order.orderStatus == '未支付'">
                            <el-button @click="submitOrder(order)" type="primary" size="mini" style="margin-top: 10px;" plain>立即支付</el-button>
                        </el-row>

                        <el-row v-if="order.orderStatus == '待评价' && order.orderType == '课程订单'">
                            <el-button @click="toCourseEvaluateViews(order.id)" type="success" size="mini" plain>评价课程</el-button>
                        </el-row>
                        <el-row v-if="order.orderStatus == '待评价' && order.orderType == '资源订单'">
                            <el-button @click="queryUserResourceScore(order.associationId)" type="success" size="mini" plain>资源评分</el-button>
                        </el-row>
                        <!-- 评价资源对话框 -->
                        <el-dialog
                        :title="'评价' + order.title"
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
                            <el-button type="primary" @click="submitResourceScore(order.associationId, resourceScore)">确 定</el-button>
                        </span>
                        </el-dialog>
                        <el-row v-if="order.orderStatus == '待评价'">
                            <el-button type="warning" @click="orderRefund(order)" size="mini" style="margin-top: 10px;" plain>商品退款</el-button>
                        </el-row>
                        <el-row v-if="order.orderStatus == '待评价'">
                            <el-button @click="deleteOrderRecord(order)" type="danger" size="mini" style="margin-top: 10px;" plain>删除记录</el-button>
                        </el-row>

                        <el-row v-if="order.orderStatus == '已完成'">
                            <el-button @click="orderRefund(order)" type="danger" size="mini" plain>商品退款</el-button>
                        </el-row>
                        <el-row v-if="order.orderStatus == '已完成'" style="margin-top: 10px;">
                            <el-button @click="deleteOrderRecord(order)" type="danger" size="mini" plain>删除记录</el-button>
                        </el-row>
                        

                        <el-row v-if="order.orderStatus == '超时已关闭'">
                            <el-button @click="deleteOrderRecord(order)" type="danger" size="mini" plain>删除记录</el-button>
                        </el-row>

                        <el-row v-if="order.orderStatus == '用户已取消'">
                            <el-button @click="deleteOrderRecord(order)" type="danger" size="mini" plain>删除记录</el-button>
                        </el-row>
                        <el-row v-if="order.orderStatus == '退款成功'">
                            <el-button @click="deleteOrderRecord(order)" type="danger" size="mini" plain>删除记录</el-button>
                        </el-row>

                        <el-row v-if="order.orderStatus == '退款失败'">
                            <el-button @click="noticeAdministrator(order.id)" type="danger" size="mini" plain>上报管理员</el-button>
                        </el-row>
                        
                    </el-col>
                    </el-row>
                </el-card>
            </el-row>

            <PagiNation 
            class="pagination"
            :totals="totals" 
            :currentPage="currentPage" 
            :pageSize="pageSize" 
            @handleCurrentChange = "handleCurrentChange"
            @handleSizeChange="handleSizeChange"/>
        </el-tabs>


        <!-- 商品退款弹窗 -->
        <el-dialog
            title="订单退款"
            :visible.sync="dialogVisible"
            width="30%"
            :before-close="handleClose">
            <el-input 
            :show-word-limit="true"
                minlength="1"
                maxlength="255"
                type="textarea"
                :autosize="{ minRows: 5, maxRows: 5}"
                v-model="reason"
                placeholder="请输入退款原因">
            </el-input>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="submitRefund()">确 定</el-button>
            </span>
            </el-dialog>
    </div>
</template>

<script>
import store from "@/store";
import $ from 'jquery'
import PagiNation from "@/components/pagination/PagiNation.vue";
export default {
    name: 'MonkeyWebOrderViews',
    components: {
        PagiNation
    },
    data() {
        return {
            // 订单中心路径
            orderCenterUrl: "http://localhost:80/monkey-user/order/center",
            // 顶单类型总数
            orderTypeTotal: [],
            // 0为VIP， 1为课程
            orderType: '0',
            // 订单状态：0表示未付款，1表示已付款, 2表示用户已取消，3表示超时已关闭
            orderStatus: "1",
            // 分页参数
            currentPage: 1,
            totals: 0,
            pageSize: 10,
            // 订单数量
            orderNumber: [],
            // 订单列表
            orderList: [],
            tabName: "all",
            // 商品退款弹窗
            dialogVisible: false,
            // 订单退款信息
            orderRefundInfo: [],
            // 退款原因
            reason: "",
            // 是否展示评价资源对话框
            showEvaluateResourceDialog: false,
            resourceScore: "",
            texts: ['不推荐', '一般推荐', '推荐', '力荐', '特别满意'],
            // 课程详细信息地址
            coursePayUrl: "http://localhost/monkey-course/pay",
            resourcePayUrl: "http://localhost/monkey-resource/pay",
            resourcePayFinishUrl: "http://localhost/monkey-resource/pay/finish",
        };
    },
    watch: {
        tabName(val) {
            this.currentPage = 1;
            if (val == 'all') {
                this.getAllOrderList();
            } else if (val == 'unpaid') {
                this.getWaitPayOrderList();
            } else if (val == 'waitEvaluate') {
                this.getWaitEvaluateOrderList();
            } else if (val == 'finished') {
                this.getAlreadyFinishedOrderList();
            } else if (val == 'userCanceled') {
                this.getUserCanceledOrderList();
            } else if (val == 'exceedTimeAlreadyClose') {
                this.getExceedTimeAlreadyCloseOrderList();
            } else if (val == 'refundSuccess') {
                this.getRefundSuccessOrderList();
            } else if (val == 'refundFail') {
                this.getRefundFailOrderList();
            }
        }
    },

    created() {
        this.getOrderTypeNumber();
        this.getAllOrderList();
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
        // 跳转至课程评价页面
        toCourseEvaluateViews(orderId) {
            this.$router.push({
                name: "course_evaluate",
                params: {
                    orderId
                }
            })
        },
        // 上报管理员
        noticeAdministrator(orderId) {
            this.$modal.msgWarning("该功能尚未完成");
        },
        // 跳转至课程详情页面或用户主页
        toDetailViews(order) {
            if (order.orderType == '课程订单') {
                // 跳转至课程详情页面
                const { href }  = this.$router.resolve({
                name: "course_detail",
                params: {
                    courseId: order.associationId
                },
            });
            window.open(href, '_blank')
            } else if (order.orderType == '用户VIP订单') {
                // 跳转至用户主页
                const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId: order.userId
                }
            })

            window.open(href, '_black');
            } else if (order.orderType == '资源订单') {
                // 跳转至资源详情页面
                const { href }  = this.$router.resolve({
                name: "resource_detail",
                params: {
                    resourceId: order.associationId
                },
                });

                window.open(href, '_blank')
            }
        },
        submitOrder(order) {
            let isSelectChargeWay = '-1';
            if (order.payWay == '支付宝支付') {
                isSelectChargeWay = '2';
            } else if (order.payWay == '微信支付') {
                isSelectChargeWay = '1';
            }
            if (order.orderType == '课程订单') {
                if (isSelectChargeWay == '2') {
                    this.courseAliPay(order.associationId, isSelectChargeWay);
                }
            } else if (order.orderType == '资源订单') {
                if (isSelectChargeWay = '2') {
                    this.resourceAliPay(order.associationId, isSelectChargeWay);
                }
            }
        },
        // 阿里云资源支付
        resourceAliPay(resourceId, payWay) {
            const vue = this;
            $.ajax({
                url: vue.resourcePayUrl + "/submitResourceOrder",
                type: "post",
                data: {
                    resourceId,
                    payWay
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        document.write(response.data);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 阿里云课程支付
        courseAliPay(courseId, isSelectChargeWay) {
            const vue = this;
            $.ajax({
                url: vue.coursePayUrl + "/tradePagePay",
                type: "post",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    courseId,
                    isSelectChargeWay
                },
                success(response) {
                    if (response.code == '200') {
                        document.write(response.data);
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 提交退款
        submitRefund() {
            if (this.reason == null || this.reason.length < 5) {
                this.$modal.msgError("输入长度至少 5 个字符");
                return;
            }
            const vue = this;
            $.ajax({
                url: vue.orderCenterUrl + "/orderRefund",
                type: "post",
                data: {
                    orderInformation: JSON.stringify(vue.orderRefundInfo),
                    reason: vue.reason
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        if (vue.tabName == 'all') {
                            vue.getAllOrderList();
                        } else if (vue.tabName == 'unpaid') {
                            vue.getWaitPayOrderList();
                        } else if (vue.tabName == 'waitEvaluate') {
                            vue.getWaitEvaluateOrderList();
                        } else if (vue.tabName == 'finished') {
                            vue.getAlreadyFinishedOrderList();
                        } else if (vue.tabName == 'exceedTimeAlreadyClose') {
                            vue.getExceedTimeAlreadyCloseOrderList();
                        } else if (vue.tabName == 'refundSuccess') {
                            vue.getRefundSuccessOrderList();
                        } else if (vue.tabName == 'refundFail') {
                            vue.getRefundFailOrderList();
                        } else if (vue.tabName == 'userCanceled') {
                            vue.getUserCanceledOrderList();
                        }
                        vue.getOrderTypeNumber();
                        vue.dialogVisible = false;
                        vue.$modal.msgSuccess(response.msg);
                    } else {
                        vue.dialogVisible = false;
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 订单退款
        orderRefund(orderInformation) {
            this.dialogVisible = true;
            this.orderRefundInfo = orderInformation;
        
        },
        // 查询用户退款失败列表
        getRefundFailOrderList() {
            const vue = this;
            $.ajax({
                url: vue.orderCenterUrl + "/getRefundFailOrderList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.totals = response.data.total;
                        vue.orderList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询用户退款成功列表
        getRefundSuccessOrderList() {
            const vue = this;
            $.ajax({
                url: vue.orderCenterUrl + "/getRefundSuccessOrderList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.totals = response.data.total;
                        vue.orderList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 用户取消订单实现
        cancelOrder(orderInformation, index) {
            const vue = this;
            this.$modal.confirm("确认取消订单？").then(() => {
                $.ajax({
                    url: vue.orderCenterUrl + "/cancelOrder",
                    type: "delete",
                    data: {
                        orderInformationId: orderInformation.id
                    },
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    success(response) {
                        if (response.code == '200') {
                            this.orderList.splice(index, 1);
                            vue.getOrderTypeNumber();
                            vue.$modal.msgSuccess(response.msg);
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            }).catch(() => { });
            
        },
        // 得到超时已关闭订单列表
        getExceedTimeAlreadyCloseOrderList() {
            const vue = this;
            $.ajax({
                url: vue.orderCenterUrl + "/getExceedTimeAlreadyCloseOrderList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.totals = response.data.total;
                        vue.orderList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })   
        },
        // 得到用户已取消订单列表
        getUserCanceledOrderList() {
            const vue = this;
            $.ajax({
                url: vue.orderCenterUrl + "/getUserCanceledOrderList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.totals = response.data.total;
                        vue.orderList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })   
        },
        // 删除订单记录
        deleteOrderRecord(orderInformation) {
            const vue = this;
            this.$modal.confirm("确定删除此订单？").then(function () {
                $.ajax({
                    url: vue.orderCenterUrl + "/deleteOrderRecord",
                    type: "delete",
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    data: {
                        orderInformationId: orderInformation.id
                    },
                    success(response) {
                        if (response.code == '200') {
                            vue.orderList.splice(orderInformation.id);
                            vue.totals--;
                            vue.getOrderTypeNumber();
                            vue.$modal.msgSuccess(response.msg);
                        } else {
                            vue.$modal.msgError(response.msg);
                        }
                    }
                })
            }).catch(() => {
                
            })
        },
        // 得到已完成订单列表
        getAlreadyFinishedOrderList() {
            const vue = this;
            $.ajax({
                url: vue.orderCenterUrl + "/getAlreadyFinishedOrderList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.totals = response.data.total;
                        vue.orderList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })   
        },
        // 得到待评价订单列表
        getWaitEvaluateOrderList() {
            const vue = this;
            $.ajax({
                url: vue.orderCenterUrl + "/getWaitEvaluateOrderList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.totals = response.data.total;
                        vue.orderList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })    
        },
        // 得到待付款订单列表
        getWaitPayOrderList() {
            const vue = this;
            $.ajax({
                url: vue.orderCenterUrl + "/getWaitPayOrderList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.totals = response.data.total;
                        vue.orderList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到全部订单列表
        getAllOrderList() {
            const vue = this;
            $.ajax({
                url: vue.orderCenterUrl + "/getAllOrderList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    currentPage: vue.currentPage,
                    pageSize: vue.pageSize,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.totals = response.data.total;
                        vue.orderList = response.data.records;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到订单类型的数量（全部，已付款，未付款，待评价）
        getOrderTypeNumber() {
            const vue = this;
            $.ajax({
                url: vue.orderCenterUrl + "/getOrderTypeNumber",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.orderNumber = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        handleSizeChange(val) {
            this.pageSize = val;
            if (this.tabName == 'all') {
                this.getAllOrderList();
            } else if (this.tabName == 'unpaid') {
                this.getWaitPayOrderList();
            } else if (this.tabName == 'waitEvaluate') {
                this.getWaitEvaluateOrderList();
            } else if (this.tabName == 'finished') {
                this.getAlreadyFinishedOrderList();
            } else if (this.tabName == 'userCanceled') {
                this.getUserCanceledOrderList();
            } else if (this.tabName == 'exceedTimeAlreadyClose') {
                this.getExceedTimeAlreadyCloseOrderList();
            } else if (this.tabName == 'refundSuccess') {
                this.getRefundSuccessOrderList();
            } else if (this.tabName == 'refundFail') {
                this.getRefundFailOrderList();
            }
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            if (this.tabName == 'all') {
                this.getAllOrderList();
            } else if (this.tabName == 'unpaid') {
                this.getWaitPayOrderList();
            } else if (this.tabName == 'waitEvaluate') {
                this.getWaitEvaluateOrderList();
            } else if (this.tabName == 'finished') {
                this.getAlreadyFinishedOrderList();
            } else if (this.tabName == 'userCanceled') {
                this.getUserCanceledOrderList();
            } else if (this.tabName == 'exceedTimeAlreadyClose') {
                this.getExceedTimeAlreadyCloseOrderList();
            } else if (this.tabName == 'refundSuccess') {
                this.getRefundSuccessOrderList();
            } else if (this.tabName == 'refundFail') {
                this.getRefundFailOrderList();
            }
        },
    },
};
</script>

<style scoped>
.pagination {
    margin-top: 10px; 
    text-align: right;
}

.el-card {
    background-color: #F7F7F7; 
    padding: 0px !important; 
    margin: 0px 0px;
    animation:slide-up 0.4s linear;
}
@keyframes slide-up {
    0% {
        opacity: 0;
        transform: translateY(100px);
    }
    60% {
        opacity: 0.6;
        transform: translateY(-10px);
    }
    100% {
        opacity: 1;
        transform: translateY(0px);
    }
}
.el-card:nth-child(n + 2) {
    margin-top: 10px;
}
.pay-kind {
    font-size: 14px;
    font-weight: 600;
    padding-top: 10px;
}
::v-deep.el-card__body {
    padding: 0;
}
.pay-way { 
    text-align: center;
}

.course-title {
    font-size: 16px;
    font-weight: 600;
    height: 65px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    /* 设置省略行 */
    -webkit-line-clamp: 3; 
    -webkit-box-orient: vertical;  
}

.course-title:hover {
    cursor: pointer;
    color: #409EFF;
}
.class-photo {
    width: 140px;
    height: 100px;
    cursor: pointer;
    transition: 0.4s linear all;
}

.class-photo:hover {
    transform: scale(1.4);
}
.order-time {
    font-size: 14px;
    color: gray;
}
.order-number {
    margin-left: 10px;
    font-size: 14px;
}
.divider {
    background-color: gray;
    height: 1px;
    width: 100%;
    margin-top: 20px;
    opacity: 0.3;
}
.MonkeyWebOrderCenterViews-container {
    width: 1200px;
    height: 100vh;
    margin: 10px auto;
}

</style>