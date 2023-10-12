<template>
    <div class="MonkeyWebResourceViews-container">
        <div class="background">
            <img class="background-image-one" src="../../assets/images/resourceViewImgTwo.webp" alt="">
            <img class="background-image-two" src="../../assets/images/resourceViewImg.webp" alt="">
            <div class="search-title">一键检索全站资源</div>
            <el-input
            v-model="searchContent"
            placeholder="输入想要搜索的资源名后，回车"
            class="search-input">
            </el-input>
            <div class="recommend-title">
                <el-button type="primary" size="small">PDF电子书</el-button>
                <el-button type="primary" size="small">Office文档</el-button>
                <el-button type="primary" size="small">热门课程</el-button>
                <el-button type="primary" size="small">压缩包</el-button>
                <el-button type="primary" size="small">word文档</el-button>
            </div>
        </div>

        <e-row>
            <el-col :span="17">
                <div class="curation-resource">
                    <div class="curation-row">
                        <div class="curation-title">精选资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-more">更多 <span class="el-icon-arrow-right"></span></div>
                    </div>
                    <div class="curation-content">
                        <div class="curation-card" v-for="(i, index) in 12" :key="index">
                            <div>
                                <img class="curation-typeImg" src="https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-bmp-file-64.png" alt="">
                            </div>
                            <div style="text-align: center;">
                                <span class="curation-achievement">
                                    <span class="el-icon-star-off">&nbsp;</span>
                                    <span class="collect-count">5.0
                                        <span style="opacity: 0.2;">|&nbsp;</span>
                                    </span>
                                
                                    <span class="el-icon-download like">&nbsp;</span>
                                    <span class="collect-count">{{ getFormatNumber(1000) }}</span>
                                </span>
                            </div>
                            <div class="curation-name">
                                测试 + {{ index }}
                            </div>
                            <div class="curation-price">
                                ￥ 12.0
                            </div>
                        </div>
                    </div>
                </div>
                <div class="curation-resource">
                    <div class="curation-row">
                        <div class="hottest-title">最热资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-nav">开发资源</div>
                        <div class="curation-more">更多 <span class="el-icon-arrow-right"></span></div>
                    </div>
                    <div class="curation-content">
                        <div class="curation-card" v-for="(i, index) in 12" :key="index">
                            <div>
                                <img class="curation-typeImg" src="https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-bmp-file-64.png" alt="">
                            </div>
                            <div style="text-align: center;">
                                <span class="curation-achievement">
                                    <span class="el-icon-view like">&nbsp;</span>
                                    <span class="collect-count">{{ getFormatNumber(1000) }}
                                        <span style="opacity: 0.2;">|&nbsp;</span>
                                    </span>
                                
                                    <span class="el-icon-download like">&nbsp;</span>
                                    <span class="collect-count">{{ getFormatNumber(1000) }}</span>
                                </span>
                            </div>
                            <div class="curation-name">
                                测试 + {{ index }}
                            </div>
                            <div class="curation-price">
                                ￥ 12.0
                            </div>
                        </div>
                    </div>
                </div>
            </el-col>
            <el-col :span="7">
                <div class="right-card">
                    <div class="latest-resource-card">
                        <div class="latest-title"></div>
                        <div class="latest-font">最新</div>
                        <div class="latest-scroll">
                            <div class="latest-card" v-for="(i, index) in 10" :key="index">
                                <div >
                                    <img class="latest-img" src="https://monkey-blog.oss-cn-beijing.aliyuncs.com/fileTypeIcon/icons8-zip-file-64.png" alt="">
                                    <span class="latest-name">资源资源名称资源名称资源名称名称</span>
                                    <span class="latest-type">免费</span>
                                    <span class="latest-time"> {{ getTimeFormat(Date.parse("2023-10-11 08:29:40")) }}</span>
                                </div>
                                <div style="margin-top: 5px;">
                                    <img class="latest-headImg"  src="https://ts1.cn.mm.bing.net/th?id=OIP-C.Zte3ljd4g6kqrWWyg-8fhAHaEo&w=316&h=197&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2" alt="">
                                    <span class="latest-username">吴吴吴吴吴吴吴吴吴吴吴吴</span>
                                    <span class="latest-down el-icon-download">&nbsp;{{ getFormatNumber(1000) }}</span>
                                    <span class="iconfont icon-shoucang latest-down">&nbsp;{{ getFormatNumber(1000) }}</span>
                                    <span class="iconfont icon-dianzan latest-down">&nbsp;{{ getFormatNumber(1000) }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="user-card">
                        <div class="user-top">资源创作排行榜</div>
                        <div class="user-rank-scroll">
                            <div class="user-content-card" v-for="(i, index) in 9" :key="index">
                                <el-row>
                                    <el-col :span="7">
                                        <span class="user-rank-score" :style="{'color': userRankColor[index]}">{{ index + 1 }}</span>
                                        <img class="user-rank-headImg" src="https://ts4.cn.mm.bing.net/th?id=OIP-C.TkprQfoHWf1_EgBAcQGO3gHaE2&w=308&h=202&c=8&rs=1&qlt=90&o=6&pid=3.1&rm=2" alt="">
                                    </el-col>
                                    <el-col :span="17">
                                        <div>
                                            <span class="user-rank-name">吴思豪吴思豪吴思豪吴思豪吴思豪吴思豪</span>
                                            <span class="user-rank-vip">vip</span>
                                        </div>
                                        <div style="margin-top: 5px;">
                                            <span class="user-rank-name">资源量&nbsp;{{ getFormatNumber(999) }}</span>
                                            <span class="user-rank-name">下载次数&nbsp;{{ getFormatNumber(999) }}</span>
                                        </div>
                                    </el-col>
                                </el-row>
                            </div>
                        </div>
                    </div>
                </div>
            </el-col>
        </e-row>
    </div>
</template>

<script>

import { getTimeFormat } from '@/assets/js/DateMethod'
import { getFormatNumber } from '@/assets/js/NumberMethod'
export default {
    name: 'MonkeyWebResourceViews',

    data() {
        return {
            searchContent: "",
            userRankColor: ["red", '#F56C6C', "#FC5531"]
        };
    },

    mounted() {
        
    },

    methods: {
        getTimeFormat(timeStamp) {
            return getTimeFormat(timeStamp);
        },
        getFormatNumber(numbers) {
            return getFormatNumber(numbers);
        },
    },
};
</script>

<style scoped>
.user-rank-scroll {
    max-height: 450px;
    overflow-y: auto;
}
.latest-scroll {
    max-height: 470px;
    overflow-y: auto;
}
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
.user-rank-vip {
    border-radius: 5px;
    color: white;
    padding: 2px 5px;
    background-image: linear-gradient(to right, #fa709a 0%, #fee140 100%);
    font-size: 14px;
    vertical-align: middle;
}
.user-rank-name {
    display: inline-block;
    vertical-align: middle;
    opacity: 0.5;
    font-size: 14px;
    margin-right: 10px;
    max-width: 120px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.user-rank-name:hover {
    color: #00f2fe;
    cursor: pointer;
}
.user-rank-score {
    font-size: 20px;
    font-weight: 600;
    vertical-align: middle;
    margin-right: 15px;
    color: rgba(0, 0, 0, 0.5);
}
.user-rank-headImg {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    vertical-align: middle;
}
.user-rank-headImg:hover {
    cursor: pointer;
    opacity: 0.5;
}
.user-content-card {
    padding: 20px;
    transition: 0.4s linear all;
    border-radius: 10px;
    vertical-align: middle;
    cursor: pointer;
}
.user-content-card:hover {
    
    background-color: rgba(0, 0, 0, 0.1);
}
.user-top {
    display: inline-block;
    background-color: rgba(8,211,252);
    padding: 10px 20px;
    border-radius: 0 20px 20px 0;
    color: white;
}
.user-card {
    margin-left: 10px;
    margin-top: 20px;
    background-color: #fff;
    box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.1);
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
.divisor {
    height: 1px;
    background-color: rgba(0, 0, 0, 0.1);
    margin: 10px;
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
    max-width: 140px;
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
    transform: scale(1.2);
}
.right-card {
    padding-top: 20px; 
}
.latest-font {
    position: absolute;
    color: white;
    top: 9px;
    left: 0px;
    font-size: 16px;
    transform: rotate(-45deg);
}
.latest-title {
    position: absolute;
    background-color: #08d3fc;
    color: white;
    top: -39px;
    left: -39px;
    height: 80px;
    width: 80px;
    text-align: center;
    line-height: 25px;
    transform: rotate(-45deg);
}
.latest-resource-card {
    position: relative;
    margin-top: 20px;
    margin-left: 10px;
    padding: 10px;
    padding-top: 20px;
    background-color: #fff;
    overflow: hidden;
    box-shadow: 0 0 3px 0 rgba(0, 0, 0, 0.1);
}
.curation-price {
    color: red;
    font-weight: 600;
    font-size: 18px;
    text-align: center;
}
.curation-achievement {
    font-size: 12px;
}
.like {
    vertical-align: middle; 
}
.collect-count {
    vertical-align: middle; 
}
.el-icon-star-off {
    font-size: 20px;
    vertical-align: middle;
    color: orange;
}
.curation-name {
    text-align: center;
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.curation-typeImg {
    width: 150px;
    height: 150px;
}
.curation-card {
    display: inline-block;
    padding: 20px;
    transition: 0.3s linear all;
    cursor: pointer;
    width: 142px;
}
.curation-card:hover {
    box-shadow: 0 0 10px 0 grey;
}
.curation-content {
    padding-left: 20px;
    font-size: 14px;
}
.curation-more {
    display: inline-block;
    font-size: 12px;
}
.curation-more:hover {
    cursor: pointer;
    opacity: 0.5;
}
.curation-nav {
    font-size: 12px;
    display: inline-block;
    padding: 5px 10px;
    margin-right: 10px;
    font-weight: 500;
    background-color: rgba(0, 0, 0, 0.1);
}
.curation-nav:hover {
    cursor: pointer;
    background-color: rgba(147,248,254);
    color: black;
    font-weight: bolder;
}
.curation-row {
    overflow-x: hidden;
    width: 100%;
    padding: 10px 20px;
}
.hottest-title {
    display: inline-block;
    background-image: linear-gradient(to right, #f83600 0%, #fff 100%);
    padding: 10px 20px;
    color: black;
    font-weight: bolder;
    font-size: 15px;
    width: 200px;
    margin-bottom: 10px;
}
.curation-title {
    display: inline-block;
    background-image: linear-gradient(to right, #89f7fe 0%, #fff 100%);
    padding: 10px 20px;
    color: black;
    font-weight: bolder;
    font-size: 15px;
    width: 200px;
    margin-bottom: 10px;
}
.curation-resource {
    position: relative;
    margin-top: 20px;
    background-color: #fff;
    padding: 20px;
}
.recommend-title {
    position: absolute;
    top: 0;
    color: white;
    opacity: 1;
    font-weight: 600;
    font-size: 30px;
    left: 50%;
    top: 75%;
    transform: translate(-50%, -50%);
}
.search-input ::placeholder {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    font-size: 14px;
    vertical-align: middle;
}
.search-input {
    position: absolute;
    top: 0;
    color: white;
    opacity: 1;
    font-size: 30px;
    left: 50%;
    top: 50%;
    width: 800px;
    transform: translate(-50%, -50%);
    font-size: 14px;
    font-weight: bolder;
    transition: 0.2s linear all;
} 
.search-input:hover {
    box-shadow: 0 0 10px 0 black;
}

.search-title {
    position: absolute;
    top: 0;
    color: white;
    opacity: 1;
    font-weight: 600;
    font-size: 30px;
    left: 50%;
    top: 30%;
    transform: translate(-50%, -50%);
}
.background-image-two {
    position: absolute;
    height: 380px;
    vertical-align: middle;
    right: 0;
    filter: brightness(0.9);
}
.background-image-one {
    height: 380px;
    vertical-align: middle;
    left: 0;
    /* 调整明亮度为50% （范围从0到1）*/
    filter: brightness(0.9);
}
.background {
    margin-top: 20px;
    position: relative;
    background-image: url("https://img-home.csdnimg.cn/images/20230209102851.png");
    background-image: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
    /* background-image: linear-gradient(90deg,#141e30,#243b55); */
    /* filter: brightness(0.8); */
}
.MonkeyWebResourceViews-container {
    width: 1200px;
    margin: 0 auto;
    height: 2000px;
}
</style>