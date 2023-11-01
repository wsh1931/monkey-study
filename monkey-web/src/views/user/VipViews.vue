<template>
    <div class="MonkeyWebVipViews-container">
        <el-row>
            <el-col :span="19" style="padding: 20px;">
                <div>
                    <div 
                    :class="['charge-card', {selectedVipPrice: selectVipPriceId == vipPrice.id}]"
                    v-for="vipPrice in vipPriceList"
                    :key="vipPrice.id"
                    @click="selectedVipPrice(vipPrice)">
                        <div class="vip-time">{{ vipPrice.day }} 天</div>
                        <div class="vip-price">${{ vipPrice.price }}</div>
                        <div class="vip-everyDay">仅 ${{ parseFloat((vipPrice.day / vipPrice.price).toFixed(2)) }}/天</div>
                    </div>
                </div>

                <div class="privilege-card">
                    <el-table
                        :data="vipPrivilegeList"
                        style="width: 100%">
                        <el-table-column
                        label="会员专属特权"
                        prop="content"
                        width="500">
                        </el-table-column>
                        <el-table-column
                        align="center">
                        <template 
                        slot="header" 
                        slot-scope="scope">
                            <span class="iconfont icon-renzhengyonghu"></span>
                            <span>&nbsp;&nbsp;用户会员</span>
                        </template>
                        <template slot-scope="scope">
                            <span class="el-icon-circle-check"></span>
                        </template>
                        </el-table-column>
                        <el-table-column
                        align="center">
                        <template 
                        slot="header" 
                        slot-scope="scope">
                            <span class="iconfont icon-yonghuguanli"></span>
                            <span>&nbsp;&nbsp;普通用户</span>
                        </template>
                        <template slot-scope="scope">
                            <span class="el-icon-circle-close"></span>
                        </template>
                        </el-table-column>
                    </el-table>
                </div>
            </el-col>
            <el-col :span="5" style="padding: 20px 0;">
                <div class="right-card">
                    <div class="order-title">订单详情</div>
                    <div class="divisor"></div>
                    <div style="margin-bottom: 20px;">
                        <div class="payWay-title">支付方式</div>
                        <div 
                        :class="['iconfont', 'icon-zhifubaozhifu', 'alipay-charge', {isSelectChargeWay: payWay == '2'}]" 
                        @click="payWay = '2'"> 支付宝支付</div>
                    </div>
                    <div class="order-monkey-header">
                        <span class="order-monkey-title">订单金额：</span>
                        <span class="order-monkey">${{ monkey }}</span>
                    </div>
                    <div>
                        <button v-if="isVip == '1'" @click="submitOrder()" class="vip-button">立即续费</button>
                        <button v-if="isVip == '2'" @click="submitOrder()" class="vip-button">开通会员</button>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery'
import store from '@/store';
export default {
    name: 'MonkeyWebVipViews',

    data() {
        return {
            selectVipPriceId: '-1',
            monkey: 0,
            payWay: "-1",
            // 会员价格列表
            vipPriceList: [],
            // 会员特权列表
            vipPrivilegeList: [],
            // 判断用户会员是否过期，1表示是会员，0表示非会员
            isVip: '1',
            vipUrl: "http://localhost/monkey-user/vip",
        };
    },

    created() {
        this.queryVipPrice();
        this.queryVipPrivilegeList();
    },

    methods: {
        // 判断用户是否为会员
        judgeIsVip() {
            const vue = this;
            $.ajax({
                url: vue.vipUrl + "/judgeIsVip",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code != Vue.ResultStatus.SUCCESS) {
                        vue.$modal.msgError(response.msg); 
                    } else {
                        vue.isVip = response.data;
                    }
                }
            })
        },
        // 提交vip订单
        submitVipOrder() {
            const vue = this;
            $.ajax({
                url: vue.vipUrl + "/submitVipOrder",
                type: "post",
                data: {
                    payWay: vue.payWay,
                    monkey: vue.monkey,
                    selectVipPriceId: vue.selectVipPriceId,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
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
        // 提交订单
        submitOrder() {
            if (this.payWay == '-1') {
                this.$modal.msgWarning("请选择付款方式");
                return;
            }
            if (this.selectVipPriceId == '-1') {
                this.$modal.msgWaring("请选择会员类型");
                return;
            }

            this.submitVipOrder();
        },
        // 查询会员专属特权列表
        queryVipPrivilegeList() {
            const vue = this;
            $.ajax({
                url: vue.vipUrl + '/queryVipPrivilegeList',
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.vipPrivilegeList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 选中课程价格
        selectedVipPrice(vipPrice) {
            this.monkey = vipPrice.price;
            this.selectVipPriceId = vipPrice.id
        },
        // 查询会员价格列表
        queryVipPrice() {
            const vue = this;
            $.ajax({
                url: vue.vipUrl + "/queryVipPrice",
                type: "get",
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.vipPriceList = response.data;
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
.vip-button {
    width: 100%;
    height: 100%;
    padding: 10px 0;
    background-color: #FFA116;
    border: #FFA116;
    border-radius: 10px;
    cursor: pointer;
    color: white;
    font-size: 18px;
    transition: 0.2s linear all;
}
.vip-button:hover {
    opacity: 0.5;
}
.order-monkey {
    color: #F56C6C;
    font-weight: 600;
    font-size: 24px;
    vertical-align: middle;
}
.order-monkey-title {
    font-size: 14px;
    vertical-align: middle;
}
.order-monkey-header {
    position: relative;
    vertical-align: middle;
    height: 40px;
    line-height: 40px;
    margin-bottom: 20px;
}
.payWay-title {
    margin-bottom: 10px;
}
.isSelectChargeWay {
    background-color: #409EFF;
    color: white;
}
.alipay-charge:hover {
    background-color: #409EFF;
    color: white;
}
.alipay-charge {
    cursor: pointer;
    padding: 20px;
    display: inline-block;
    box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.1);
    transition: 0.4s linear all;
    border-radius: 10px;
}
.order-title {
    margin-bottom: 16px;
}
.divisor {
    height: 1px;
    background-color: rgba(0, 0, 0, 0.1);
    margin-bottom: 16px;
}
.right-card {
    box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.1);
    padding: 20px 10px;
    border-radius: 10px;
    background-color: #fff;
}
.el-icon-circle-close {
    background-color: grey;
    color: white;
    font-size: 30px;
    border-radius: 50%;
}
.el-icon-circle-check {
    background-color: #67C23A;
    color: white;
    font-size: 30px;
    border-radius: 50%;
}
.icon-yonghuguanli {
    font-size: 22px;
    vertical-align: middle;
}
.icon-renzhengyonghu {
    vertical-align: middle;
    font-size: 22px;
    color: rgba(255,161,22);
}
.privilege-card {
    margin-top: 10px;
    padding: 20px;
    box-shadow: 0 0 10px 0 rgba(1, 1, 1, 0.1);
    background-color: #fff;
}
.vip-everyDay {
    color: gray;
    font-size: 14px;
}
.vip-price {
    color: rgba(255,161,22);
    font-size: 24px;
    margin-bottom: 10px;
}
.vip-time {
    margin-bottom: 10px;
    color: gray;
}
.charge-card {
    display: inline-block;
    padding: 20px;
    box-shadow: 0 0 10px 0 rgba(1, 1, 1, 0.1);
    background-color: #fff;
    width: 189px;
    text-align: center;
    border-radius: 10px;
    transition: 0.3s linear all;
    cursor: pointer;
    margin-right: 10px;
    margin-bottom: 10px;
    background-color: #fff;
}
.charge-card:nth-child(4n) {
    margin-right: 0;
}
.charge-card:hover {
    box-shadow: 0 0 5px 0 rgba(255,161,22);
}
.selectedVipPrice {
    box-shadow: 0 0 5px 0 rgba(255,161,22);
}

.MonkeyWebVipViews-container {
    margin: 20px auto;
    width: 1250px;
}
</style>