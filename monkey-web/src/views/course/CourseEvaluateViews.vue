<template>
    <div class="MonkeyWebCourseEvaluateViews-container">
        <el-row class="order-title">订单评价页面</el-row>
        <el-row>
            <el-col :span="12" class="order-number">
                订单号：{{ order.id }}
            </el-col>
            <el-col :span="12" class="order-date">
                支付时间：{{ order.payTime }}
            </el-col>
        </el-row>

        <el-row class="order-content">
            <el-row >
                <el-col :span="6" style="text-align: center;">
                    <el-row class="order-img-row">
                        <img class="order-img" :src="order.picture" alt="">
                    </el-row>
                    <el-row class="course-title">
                        {{ order.title }}
                    </el-row>
                    <el-row class="order-money">
                        $ {{ order.orderMoney }}
                    </el-row>
                </el-col>
                
                <el-col :span="2">
                    
                    <el-row class="divider"></el-row>
                    &nbsp;
                </el-col>
                
                
                <el-col :span="16" style="text-align: left;">
                    <el-row>
                        <el-col :span="2">
                            商品评分
                        </el-col>
                        <el-col :span="22" style="padding-left: 10px;">
                            <el-rate
                                v-model="value"
                                :texts=texts
                                show-text
                                :colors="colors"
                                text-color="#FC5531">
                            </el-rate>
                        </el-col>
                    </el-row>

                    <!-- 评价标签 -->
                    <el-row style="padding: 20px 0;">
                        <el-col :span="2" style="margin-top: 10px;">
                            添加标签
                        </el-col>
                        <el-col :span="22" style="padding-left: 10px;">
                            <el-tag
                                effect="plain"
                                type="info"
                                :key="tag.id"
                                v-for="tag in dynamicTags"
                                :disable-transitions="false"
                                @click="selectedTag(tag)"
                                :class="['label', {selected: tag.isSelected == '1'}]">
                                {{tag.name}}
                            </el-tag>
                            <el-input
                                class="input-new-tag"
                                v-if="inputVisible"
                                v-model="inputValue"
                                ref="saveTagInput"
                                size="small"
                                @keyup.enter.native="handleInputConfirm()"
                                @blur="handleInputConfirm()"
                            >
                            </el-input>
                            <el-button v-else class="button-new-tag" size="small" @click="showInput">+ 添加长度为 4 的标签（三个）</el-button>
                        </el-col>
                        
                    </el-row>

                    <!-- 评价内容 -->
                    <el-row>
                        <el-col :span="2">
                            评价内容
                        </el-col>
                        <el-col :span="22" style="padding-left: 10px;">
                            <el-input 
                                :show-word-limit="true"
                                    minlength="1"
                                    maxlength="255"
                                    type="textarea"
                                    :autosize="{ minRows: 5, maxRows: 5}"
                                    max="100"
                                    v-model="evaluateContent"
                                    placeholder="请输入您对此课程的评价">
                            </el-input>
                        </el-col>
                    </el-row>
                </el-col>
            </el-row>

            <el-row class="submit-evaluate">
                <el-button type="primary" style="border-radius: 20px;" @click="submitEvaluate(order.associationId)">
                    提交评价
                </el-button>
            </el-row>
        </el-row>
    </div>
</template>

<script>
import store from '@/store';
import $ from 'jquery'
export default {
    name: 'MonkeyWebOrderEvaluateViews',

    data() {
        return {
            value: null,
            // 评分颜色
            colors: ["grey", "orange", "red"],
            // 评分描述内容
            texts: ['不推荐', '一般推荐', '推荐', '力荐', '特别满意'],

            dynamicTags: [],
            // 是否展示标签输入框，
            inputVisible: false,
            // 标签输入框内容
            inputValue: '',
            // 选中的标签列表
            selectedTags: [],
            // 课程评价内容
            evaluateContent: "",
            // 订单id
            orderId: "",
            // 课程支付完成地址
            coursePayUrl: "http://localhost/monkey-course/pay/finish",
            // 订单信息
            order: "",
            // 课程评价地址
            courseEvaluateUrl: "http://localhost/monkey-course/evaluate",
        };
    },

    created() {
        this.orderId = this.$route.params.orderId;
        this.queryOrderInfoByOrderId(this.orderId);
        this.queryEvaluateLabelList();
    },

    methods: {
        // 提交评价
        submitEvaluate(courseId) {
            if (this.selectedTags.length != 3) {
                this.$modal.msgError("只能选则三个标签");
                return;
            }
            if (this.value == null || this.value == "") {
                this.$modal.msgError("还未给课程评分");
                return;
            }
            if (this.evaluateContent == "") {
                this.$modal.msgError("课程评价内容不能为空");
                return;
            }
            const vue = this;
            $.ajax({
                url: vue.courseEvaluateUrl + "/submitEvaluate",
                type: "post",
                data: {
                    courseId,
                    score: vue.value,
                    selectedTags: JSON.stringify(vue.selectedTags),
                    evaluateContent: vue.evaluateContent,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$modal.msgSuccess(response.msg);
                        vue.$router.push({
                            name: 'course_pay_finish',
                            params: {
                                orderId: vue.order.id,
                            }
                        })
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 得到评价标签列表
        queryEvaluateLabelList() {
            const vue = this;
            $.ajax({
                url: vue.courseEvaluateUrl + "/queryEvaluateLabelList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.dynamicTags = response.data
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
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
                        vue.order = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 选中标签
        selectedTag(tag) {
            tag.isSelected = '1';
            this.selectedTags.push(tag.name);
        },
        showInput() {
            this.inputVisible = true;
            this.$nextTick(_ => {
            this.$refs.saveTagInput.$refs.input.focus();
        });
        },

    handleInputConfirm() {
        let inputValue = this.inputValue;
        console.log(inputValue)
        if (inputValue.length == '4') {
            this.dynamicTags.push(inputValue);
        } else if (inputValue != '' && inputValue.length != 4){
            this.$modal.msgError("标签长度只能为4")
        }
        this.inputVisible = false;
        this.inputValue = '';
    }
    },
};
</script>

<style scoped>
.submit-evaluate {
    padding-top: 40px;
    text-align: center;
}
.label {
    cursor: pointer;
    margin-top: 5px;
}
.label:hover {
    color: white;
    background-color: lightgreen;
}
.selected {
    color: white !important;
    background-color: lightgreen !important;
}
.el-tag + .el-tag {
    margin-left: 10px;
}
.button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
}
.input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
}
.divider {
    height: 280px;
    width: 1px;
    background-color: grey;
    position: absolute;
    opacity: 0.5;
    left: 340px;
}
.course-title {
    font-weight: 600;
    padding: 20px 0;
}
.order-money {
    color: red;
}
.order-img-row {
    overflow: hidden;
}
.order-img {
    width: 100%;
    height: 100%;
    cursor: pointer;
    transition: 0.4s linear all;
}
.order-img:hover {
    transform: scale(1.4);
}
.order-content {
    background-color: #FFFFFF;
    padding: 20px;
}
.order-number {
    font-size: 14px;
    text-align: right;
    padding: 20px;
}
.order-date {
    font-size: 14px;
    text-align: left;
    padding: 20px;
}
.order-title {
    font-size: 20px;
    text-align: center;
    font-weight: 600;
}
.MonkeyWebCourseEvaluateViews-container {
    width: 1200px;
    margin: 20px auto;
}
</style>