<template>
    <div class="MonkeyWebResourceDetail-container">
        <el-row>
            <!-- 左侧内容 -->
            <el-col :span="17" class="left-card">
                <div class="content-card">
                    <div class="resource-title">
                        <span style="margin-right: 10px;">{{ resource.name }}</span>
                        <!-- todo -->
                        <el-tooltip class="item" effect="light" content="该资源内容由用户上传，如若侵权请联系客服进行举报" placement="top">
                            <el-tag size="medium" type="danger" class="copyright-appeal">版权申诉</el-tag>
                        </el-tooltip>
                        
                    </div>
                    <div style="margin-bottom: 10px;">
                        <img @click="toUserViews(resource.userId)" class="user-headImg" src="https://ts4.cn.mm.bing.net/th?id=OIP-C.WkkIXkr-QmHImU57KtkYQgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2" alt="">
                        <span @click="toUserViews(resource.userId)" class="username">{{ resource.username }}</span>
                        <span class="el-icon-time createTime">&nbsp;{{ getTimeFormat(resource.createTime) }}</span>
                        <el-tag size="medium" class="label" type="info">{{ resource.resourceClassificationName }}</el-tag>
                    </div>
                    <div style="margin-bottom: 20px;">
                        <el-tag 
                        size="medium" 
                        class="classification" 
                        v-for="resourceLabel in resource.resourceLabel" 
                        :key="resourceLabel">{{ resourceLabel }}</el-tag>
                    </div>
                    <div class="brief-card">
                        <span class="brief-header"></span>
                        <span class="brief-title">资源描述</span>
                        <span class="brief-content">{{ resource.description }}</span>
                    </div>

                    <div class="resource-border">
                        <div class="resource-card">
                            <div style="margin-bottom: 20px;">
                                <span>资源类型：</span>
                                <span class="resource-type">{{ resource.type }}</span>
                            </div>
                            <div style="margin-bottom: 20px;">
                                <span>形式类型：</span>
                                <span v-if="resource.formTypeId == '1'" class="resource-formType">免费</span>
                                <span v-else-if="resource.formTypeId == '2'" class="resource-formType">会员免费</span>
                                <span v-else-if="resource.formTypeId == '3'" class="resource-formType">收费</span>
                            </div>
                            <div style="margin-bottom: 20px;">
                                <span>资源金额：</span>
                                <span class="resource-price" v-if="resource.formTypeId == '1'">
                                    <span>免费</span>
                                </span>
                                <span class="resource-price" v-if="resource.formTypeId == '2'">
                                    <span>会员免费</span>
                                </span>
                                <span class="resource-price" v-if="resource.formTypeId == '3'">
                                    <span style="margin-right: 10px;">￥2.1</span>
                                    <span v-if="resource.isDiscount == '1'" class="resource-before-price">{{ resource.originPrice }}</span>
                                </span>
                            </div>
                            
                            <div>
                                <button
                                v-if="isAuthorization == '1'"
                                @click="downResource(resource)"
                                class="buy-button el-icon-download" >&nbsp;下载资源</button>
                                <!-- <a 
                                v-if="isAuthorization == '1'"
                                :href="resourceDetailUrl + '/downFileResource?' + 'resourceId=' + resourceId" 
                                class="buy-button el-icon-download" >&nbsp;下载资源</a> -->
                                <button
                                @click="toVipViews()"
                                class="buy-button" 
                                v-if="resource.formTypeId == '2' && isAuthorization == '0'" >开通会员</button>
                                <button
                                @click="toResourcePayViews(resource.id)"
                                class="buy-button" 
                                v-if="resource.formTypeId == '3' && isAuthorization == '0'" >购买资源</button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="bottom-card">
                    <span style="margin-right: 20px; font-size: 15px;" class="el-icon-view">&nbsp;游览&nbsp;{{ getFormatNumber(999) }}</span>
                    <span style="margin-right: 20px; font-size: 15px;" class="iconfont icon-dianzan">&nbsp;点赞&nbsp;{{ getFormatNumber(999) }}</span>
                    <span style="margin-right: 20px; font-size: 15px;" class="iconfont icon-shoucang">&nbsp;收藏&nbsp;{{ getFormatNumber(999) }}</span>
                    <span style="margin-right: 20px; font-size: 15px;;" class="iconfont icon-pinglun">&nbsp;回复&nbsp;{{ getFormatNumber(999) }}</span>
                    <span style="margin-right: 20px; font-size: 15px;" class="iconfont icon-zhuanfa">&nbsp;转发&nbsp;{{ getFormatNumber(999) }}</span>
                    <span style="margin-right: 20px; font-size: 15px;" class="el-icon-warning-outline">&nbsp;举报</span>
                    <el-button type="primary" class="reply-button el-icon-edit" size="mini" round>&nbsp;写回复</el-button>
                </div>
                <div class="resource-comment-card">
                    <ResourceComment/>
                </div>
            </el-col>
            <!-- 右侧内容 -->
    
            <el-col :span="7">
            <div class="right-card">
                <div>
                    <div class="article-score-title">资源评分</div>
                    <div class="divider"></div>
                    <div style="padding: 16px;">
                        <el-row>
                            <el-col :span="6">
                                <span class="article-score">{{ resourceEvaluateInfo.scoreCount }}</span>
                            </el-col>
                            <el-col :span="18">
                                <el-rate
                                    v-model="value"
                                    disabled
                                    text-color="#ff9900"
                                    score-template="{value}">
                                </el-rate>
                                <el-button type="text" style="font-size: 16px;">{{ getFormatNumber(resourceEvaluateInfo.userCount) }} 个用户评价</el-button>
                            </el-col>
                        <el-row>
                            <el-col :span="3">
                                <span style="font-size: 14px;">1星</span>
                            </el-col>
                            <el-col :span="21">
                                <el-progress color="#FEDDD5"
                                    text-color="black" 
                                    :text-inside="true" 
                                    :stroke-width="20" 
                                    :percentage="resourceEvaluateInfo.oneScore" 
                                    show-text>
                                </el-progress>
                            </el-col>
                        </el-row>
                        <el-row style="margin-top: 5px;">
                            <el-col :span="3">
                                <span style="font-size: 14px;">2星</span>
                            </el-col>
                            <el-col :span="21">
                                <el-progress color="#FEDDD5"
                                    text-color="black" 
                                    :text-inside="true" 
                                    :stroke-width="20" 
                                    :percentage="resourceEvaluateInfo.twoScore" 
                                    show-text>
                                </el-progress>
                            </el-col>
                        </el-row>
                        <el-row style="margin-top: 5px;">
                            <el-col :span="3">
                                <span style="font-size: 14px;">3星</span>
                            </el-col>
                            <el-col :span="21">
                                <el-progress color="#FEDDD5"
                                    text-color="black" 
                                    :text-inside="true" 
                                    :stroke-width="20" 
                                    :percentage="resourceEvaluateInfo.threeScore" 
                                    show-text>
                                </el-progress>
                            </el-col>
                        </el-row>
                        <el-row style="margin-top: 5px;">
                            <el-col :span="3">
                                <span style="font-size: 14px;">4星</span>
                            </el-col>
                            <el-col :span="21">
                                <el-progress color="#FEDDD5"
                                    text-color="black" 
                                    :text-inside="true" 
                                    :stroke-width="20" 
                                    :percentage="resourceEvaluateInfo.fourScore" 
                                    show-text>
                                </el-progress>
                            </el-col>
                        </el-row>
                        <el-row style="margin-top: 5px;">
                            <el-col :span="3">
                                <span style="font-size: 14px;">5星</span>
                            </el-col>
                            <el-col :span="21">
                                <el-progress color="#FEDDD5"
                                    text-color="black" 
                                    :text-inside="true" 
                                    :stroke-width="20" 
                                    :percentage="resourceEvaluateInfo.fiveScore" 
                                    show-text>
                                </el-progress>
                            </el-col>
                        </el-row>
                        </el-row>
                    </div>
                </div>

                <div class="user-card">
                        <div class="user-top">
                            <span style="margin-right: 10px;">相关资源</span>
                            <el-tooltip class="item user-rank-tip" effect="dark" content="根据资源分类来查找相关资源" placement="top">
                                <span class="el-icon-question"></span>
                            </el-tooltip>
                        </div>
                        <div class="latest-scroll">
                            <div 
                            class="latest-card" 
                            v-for="relateResource in relateResourceList" 
                            :key="relateResource.id"
                            @click.stop="toResourceDetail(relateResource.id)" >
                                <div >
                                    <img 
                                    @click.stop="toResourceDetail(relateResource.id)" 
                                    class="latest-img" 
                                    src="https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-no-file-64.png" alt="">
                                    <span 
                                    @click.stop="toResourceDetail(relateResource.id)"  
                                    class="latest-name">吴思豪</span>
                                    <span class="latest-type" v-if="relateResource.formTypeId == '1'">免费</span>
                                    <span class="latest-vip" v-if="relateResource.formTypeId == '2'">vip</span>
                                    <span class="latest-price" v-if="relateResource.formTypeId == '3'">￥{{ relateResource.price }}</span>
                                    <span class="latest-time"> {{ getTimeFormat(relateResource.createTime) }}</span>
                                </div>
                                <div style="margin-top: 5px;">
                                    <img @click.stop="toUserViews(relateResource.userId)" class="latest-headImg"  src="https://ts4.cn.mm.bing.net/th?id=OIP-C.WkkIXkr-QmHImU57KtkYQgHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2" alt="">
                                    <span @click.stop="toUserViews(relateResource.userId)" class="latest-username">啥也不会啥也不会hh啥也不会hh啥也不会hh啥也不会hhhh</span>
                                    <span class="latest-down el-icon-download">&nbsp;{{ getFormatNumber(relateResource.downCount) }}</span>
                                    <span class="iconfont icon-shoucang latest-down">&nbsp;{{ getFormatNumber(relateResource.collectCount) }}</span>
                                    <span class="iconfont icon-dianzan latest-down">&nbsp;{{ getFormatNumber(relateResource.likeCount) }}</span>
                                </div>
                            </div>
                        </div>
                        <div v-if="relateResourceList == null || relateResourceList == '' || relateResourceList.length <= 0">
                            <el-empty description="暂无资源"></el-empty>
                        </div>
                    </div>
            </div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import $ from 'jquery';
import store from '@/store';
import { getTimeFormat } from '@/assets/js/DateMethod'
import ResourceComment from '@/views/resource/ResourceComment'
import { getFormatNumber } from '@/assets/js/NumberMethod';
export default {
    name: 'MonkeyWebResourceDetail',
    components: {
        ResourceComment
    },
    data() {
        return {
            value: '3.5',
            formTypeId: '1',
            resourceId: "",
            resource: [],
            isAuthorization: "是否有权限下载资源",
            resourceEvaluateInfo: {},
            relateResourceList: [],
            resourceDetailUrl: "http://localhost:80/monkey-resource/detail"
        };
    },

    created() {
        this.resourceId = this.$route.params.resourceId;
        this.queryResourceInfo(this.resourceId);
        this.queryResourceEvaluateInfo(this.resourceId);
        this.queryRelateResourceList(this.resourceId);
    },

    methods: {
        // 前往课程详情页面
        toResourceDetail(resourceId) {
            const { href } = this.$router.resolve({
                name: "resource_detail",
                params: {
                    resourceId
                }
            })

            window.open(href, "_blank")
        },
        // 查询相关资源列表
        queryRelateResourceList(resourceId) {
            const vue = this;
            $.ajax({
                url: vue.resourceDetailUrl + "/queryRelateResourceList",
                type: "get",
                data: {
                    resourceId
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.relateResourceList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 查询资源评价信息
        queryResourceEvaluateInfo(resourceId) {
            const vue = this;
            $.ajax({
                url: vue.resourceDetailUrl + "/queryResourceEvaluateInfo",
                type: "get",
                data: {
                    resourceId
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.resourceEvaluateInfo = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 前往资源支付页面
        toResourcePayViews(resourceId) {
            this.$router.push({
                name: "resource_pay",
                params: {
                    resourceId
                }
            })
        },
        // 前往用户主页
        toUserViews(userId) {
            const { href } = this.$router.resolve({
                name: "user_home",
                params: {
                    userId
                }
            })

            window.open(href, "_blank")
        },
        toVipViews() {
            this.$router.push({
                name: 'vip',
            })
        },
        downFileResource(resource) {
            const vue = this;
            const url = this.resourceDetailUrl + '/downFileResource?' + 'resourceId=' + resource.id; //记得拼接参数
            $.ajax({
                url: url,
                type: 'POST',
                beforeSend: function(xhr) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('token'));
                    xhr.setRequestHeader('Content-Type', 'application/octet-stream');
                },
                xhrFields: {
                    responseType: 'blob'
                },
                success: function (response, status, xhr) {
                    const blob = response;
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        const a = document.createElement('a');
                        a.download = resource.name;
                        a.href = e.target.result;
                        document.documentElement.appendChild(a);
                        a.click();
                        a.remove();
                    };
                    reader.readAsDataURL(blob);
                },
                error: function (xhr, status, error) {
                    vue.$modal.msgError(ExceptionEnum.getMsg(xhr.status));
                }
            });

        },
        // 下载资源
        downResource(resource) {
            if (this.isAuthorization == '1') {
                // 有权限，下载资源
                // window.location.href = this.resource.url
                this.downFileResource(resource);
            } else if (this.isAuthorization == '0') {
                // 无权限
                if (this.resource.formTypeId == '2') {
                    // 若为会员类型，前往开通会员界面
                    
                } else if (this.resource.formTypeId == '3') {
                    // 前往资源收费界面
                }

                return false;
            }
        },
        // 查询资源信息
        queryResourceInfo(resourceId) {
            const vue = this;
            $.ajax({
                url: vue.resourceDetailUrl + "/queryResourceInfo",
                type: "get",
                data: {
                    resourceId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.resource = response.data.resourcesVo;
                        vue.isAuthorization = response.data.isAuthorization
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        getTimeFormat(timeStamp) {
            return getTimeFormat(timeStamp);
        },
        getFormatNumber(num) {
            return getFormatNumber(num);
        }
    },
};
</script>

<style scoped>
::-webkit-scrollbar {
    width: 10px;
    background-color: #fff;
}

:hover ::-webkit-scrollbar-track-piece {
    /* 鼠标移动上去再显示滚动条 */
    background-color: #fff;
    /* 滚动条的背景颜色 */
    border-radius: 6px;
    /* 滚动条的圆角宽度 */
}

:hover::-webkit-scrollbar-thumb:hover {
    background-color: rgba(0, 0, 0, 0.1);
}

:hover::-webkit-scrollbar-thumb:vertical {
    background-color: rgba(0, 0, 0, 0.1);
    border-radius: 6px;
    outline: 2px solid #fff;
    outline-offset: -2px;
    border: 2px solid #fff;
}
.latest-down {
    font-size: 14px;
    opacity: 0.5;
    vertical-align: middle;
    margin-right: 10px;
}
.latest-username {
    display: inline-block;
    font-size: 14px;
    opacity: 0.5;
    vertical-align: middle;
    margin-right: 5px;
    max-width: 70px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.latest-username:hover {
    cursor: pointer;
    color: #00f2fe;
}
.latest-headImg {
    width: 35px;
    height: 35px;
    vertical-align: middle;
    transition: 0.4s linear all;
    margin-right: 10px;
    border-radius: 50%;
}
.latest-headImg:hover {
    cursor: pointer;
    transform: scale(1.2);
}
.latest-time {
    font-size: 14px;
    opacity: 0.5;
    vertical-align: middle;
}
.latest-vip {
    border-radius: 5px;
    color: white;
    padding: 2px 5px;
    background-image: linear-gradient(to right, #fa709a 0%, #fee140 100%);
    font-size: 14px;
    vertical-align: middle;
}
.latest-price {
    display: inline-block;
    padding: 2px 5px;
    text-align: center;
    background-color: #f90a0a;
    color: #fff;
    border-radius: 5px;
    font-size: 14px;
}
.latest-type {
    display: inline-block;
    margin-right: 10px;
    vertical-align: middle;
    padding: 2px 5px;
    font-size: 14px;
    color: white;
    background-color: #00f2fe;
    border-radius: 5px;
}
.latest-name {
    display: inline-block;
    opacity: 0.5;
    vertical-align: middle;
    margin-right: 10px;
    max-width: 130px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    font-size: 14px;
}
.latest-name:hover {
    cursor: pointer;
    color: #00f2fe;
}
.latest-img {
    width: 35px;
    height: 35px;
    vertical-align: middle;
    transition: 0.4s linear all;
    margin-right: 10px;
}
.latest-img:hover {
    cursor: pointer;
    transform: scale(1.5);
}
.latest-card {
    border-radius: 10px;
    padding: 10px;
    transition: 0.4s linear all;
}
.latest-card:hover {
    cursor: pointer;
    background-color: rgba(0, 0, 0, 0.1);
}
.latest-scroll {
    max-height: 470px;
    overflow-y: auto;
}
.user-rank-tip {
    cursor: pointer;
}
.user-top {
    display: inline-block;
    background-color: rgba(8,211,252);
    padding: 10px 20px;
    border-radius: 0 20px 20px 0;
    color: white;
    font-size: 15px;
}
.user-card {
    margin-left: 10px;
    margin-top: 20px;
    background-color: #fff;
}
.right-card {
    padding: 10px;
    background-color: #fff;
    margin-left: 10px;
    box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.1);
}
.article-score {
    font-weight: 600;
    font-size: 34px;
    color: #F56C6C;
}
.divider {
    height: 1px;
    background: #e8e8ed;
}
.article-score-title {
    font-weight: 600;
    padding: 16px;
}
.content-card {
    background-color: #fff;
    box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.1);
    padding: 20px;

}
.reply-button {
    position: absolute;
    right: 10px;
    top: 10px;
}
.bottom-card {
    display: inline-block;
    position: fixed;
    bottom: 0;
    padding: 10px 20px;
    box-shadow: 0 0 5px 0 rgba(0, 0, 0, 0.2);
    vertical-align: middle;
    width: 810px;
    z-index: 1;
    background-color: #fff;
}
.copyright-appeal {
    vertical-align: middle;
    cursor: pointer;
}
.copyright-appeal:hover {
    opacity: 0.5;
}
.resource-type {
    padding: 5px 5px;
    font-size: 14px;
    font-weight: 600;
    color: white;
    border-radius: 5px;
    background-image: linear-gradient(to right, #a3e4f6 0%, #adb3fa 100%);
}
.resource-before-price {
    text-decoration: line-through;
    color: white;
    font-weight: normal !important;
}
.buy-button {
    background-image: linear-gradient(to right, #ff5858 0%, #f09819 100%);
    border: none !important;
    padding: 10px 20px;
    border-radius: 20px;
    color: black;
    transition: 0.4s linear all;
}
.buy-button:hover {
    cursor: pointer;
    color: white;
    font-weight: 600;
    opacity: 0.9;
}
.resource-price {
    padding: 5px 5px;
    border-radius: 5px;
    font-weight: 600;
    background-image: linear-gradient(to right, #f0b9c7 0%, #facabc 100%);
}
.resource-formType {
    padding: 5px 5px;
    border-radius: 5px;
    font-weight: 600;
    background-image: linear-gradient(to right, #fbbed0 0%, #f5e9a6 100%);
}
.resource-border {
    text-align: center;
    font-size: 14px;
    margin-bottom: 20px;
}
.resource-card {
    text-align: left;
    display: inline-block;
    padding: 20px;
    background-color: rgba(0,242,254, 0.5);
    width: 400px;
    border-radius: 10px;
}

.brief-content {
    font-size: 14px;
    margin-right: 10px;
    opacity: 0.8;
}
.brief-title {
    font-size: 14px;
    opacity: 0.5;
    margin-right: 10px;
}
.brief-header {
    background-color: rgba(0,242,254, 0.5);
    padding: 10px 5px;
    margin-right: 10px;
    border-radius: 10px;
}
.brief-card {
    margin-bottom: 10px;
    background-color: rgba(0,242,254, 0.2);
    padding: 10px 0;
    border-radius: 10px;
}
.classification {
    margin-right: 10px;
    cursor: pointer;
    transition: 0.2s linear all;
}
.classification:hover {
    opacity: 0.5;
}
.label {
    vertical-align: middle;
    font-size: 14px;
}
.createTime {
    vertical-align: middle;
    font-size: 14px;
    color: gray;
    margin-right: 10px;
}
.username {
    display: inline-block;
    font-size: 14px;
    transition: 0.2s linear all;
    margin-right: 10px;
    vertical-align: middle;
    max-width: 530px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.username:hover {
    cursor: pointer;
    opacity: 0.5;
}
.user-headImg {
    border-radius: 50%;
    width: 40px;
    height: 40px;
    transition: 0.2s linear all;
    vertical-align: middle;
    margin-right: 10px;
}
.user-headImg:hover {
    cursor: pointer;
    filter: brightness(1.5);
}
.resource-title {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 10px;
}
.left-card {
    position: relative;
}
.MonkeyWebResourceDetail-container {
    margin: 20px auto;
    width: 1200px;
}
</style>