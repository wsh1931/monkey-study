<template>
    <div class="MonkeyWebResourcePay-container">
        <el-steps :active="1" align-center finish-status="process">
            <el-step title="确认订单信息" description=""></el-step>
            <el-step title="提交订单" description=""></el-step>
            <el-step title="订单完成" description=""></el-step>
            <el-step title="商品评价" description=""></el-step>
        </el-steps>

        <el-row style="margin-top: 30px;">
            <el-row style="margin-top: 10px;">
                <el-col :span="7">
                    <img class="course-img" :src="resourceInfo.typeUrl" alt="">
                </el-col>
                <el-col :span="17" style="padding-left: 10px;">
                    <el-row class="course-title">{{ resourceInfo.name }}</el-row>
                    <el-row class="course-profile">
                        {{ resourceInfo.description }}
                    </el-row>
                    <el-row style="margin-top: 10px;">
                        <el-col :span="8">
                            <span class="first-letter">{{ resourceInfo.originPrice }}</span>
                        </el-col>
                        <el-col :span="8">
                                <span class="el-icon-notebook-1" style="font-size: 14px;">共&nbsp;{{resourceInfo.collectCount}}&nbsp;人收藏</span>
                        </el-col>
                        <el-col :span="8">
                            <span class="el-icon-download" style="font-size: 14px;">&nbsp;{{ resourceInfo.downCount }}&nbsp;人下载</span>
                        </el-col>
                    </el-row>
                </el-col>
            </el-row>
        </el-row>


        <!-- 资源提醒 -->
        <el-row style="padding-top: 20px;">
            资源提醒发送至
        </el-row>
        <el-row style="padding: 20px 0;" v-if="resourceInfo.hasEmail == '1'">
            QQ邮箱：{{ resourceInfo.email }}
        </el-row>
        <el-row v-if="resourceInfo.hasEmail == '0'">
            <el-button type="text" @click="toUserViews($.store.state.user.id)">去个人主页绑定QQ邮箱</el-button>
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
                ${{ resourceInfo.originPrice }}
            </el-col>
        </el-row>
        <el-row class="monkey">  
            <el-col :span="2">
                应付金额
            </el-col>
            <el-col :span="20" style="color: red;">
                ${{ resourceInfo.price }}
            </el-col>
        </el-row>

        <el-row style="text-align: right;">
            <el-button type="primary" class="submit-button" @click="submitResourceOrder(resourceId, isSelectChargeWay)">提交订单</el-button>
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
import { getFormatNumber } from '@/assets/js/NumberMethod';
export default {
    name: 'MonkeyWebResourcePay',

    data() {
        return {
            isSelectChargeWay: -1,
            resourceId: "",
            resourcePayUrl: "http://localhost/monkey-resource/pay",
            resourceInfo: {},
        };
    },

    created() {
        this.resourceId = this.$route.params.resourceId;
        this.queryResourceInfo(this.resourceId);
    },

    methods: {
        // 提交资源订单
        submitResourceOrder(resourceId, payWay) {
            if (this.isSelectChargeWay == '-1') {
                this.$modal.msgWarning("请选择支付方式");
                return false;
            }
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
        // 得到资源基本信息
        queryResourceInfo(resourceId) {
            const vue = this;
            $.ajax({
                url: vue.resourcePayUrl + "/queryResourceInfo",
                type: "get",
                data: {
                    resourceId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.resourceInfo = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        getFormatNumber(val) {
            return getFormatNumber(val);
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
.MonkeyWebResourcePay-container {
    background-color: #FFFFFF;
    width: 1000px;
    margin: 20px auto;
    padding: 20px;
}
</style>