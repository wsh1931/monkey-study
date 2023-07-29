<template>
    <div class="MonkeyWebCourseDetail-container">
        <el-row class="all-background">
        <el-row class="top">
            <el-col :span="16">
                <el-row class="ellipsis-title title">
                   python从0到1：期货量化交易系统（CTP实战，高python从0到1：期货量化交易系统（CTP实战，高
                </el-row>
                <el-row style="color: white; text-align: center; padding: 20px 0px;">
                    <el-col :span="4">
                        
                        <el-row >
                            356
                        </el-row>
                        <el-row>
                            课程收藏数
                        </el-row>
                     <span class="divider-left"></span>
                    </el-col>
                    <el-col :span="6">
                        <el-row>
                            48节
                        </el-row>
                        <el-row>
                            节数
                        </el-row>
                        <span class="divider-left"></span>
                    </el-col>
                    <el-col :span="6">
                        <el-row>
                            9057人
                        </el-row>
                        <el-row>
                            学习人数
                        </el-row>
                        <span class="divider-left"></span>
                    </el-col>
                    <el-col :span="6">
                        <el-row>
                            <el-rate
                                v-model="value"
                                disabled
                                show-score
                                text-color="#ff9900"
                                score-template="{value}">
                                </el-rate>
                        </el-row>
                        <el-row>
                            课程评分
                        </el-row>
                    </el-col>
                </el-row>
                <el-row style=" padding-bottom: 20px; margin-left: 30px;">
                    <el-col :span="6">
                    <el-button type="primary" class="free-view" > <i class="el-icon-view"></i> 免费试看</el-button>
        
                    </el-col>
                    <el-col :span="6">
                        <el-button type="danger" class="buy-view">238.00 购买</el-button>
                    </el-col>
                    <el-col :span="4" class="discount">
                        课程8.0折
                    </el-col>
                    <el-col :span="6" class="original-price">
                        原价￥298
                    </el-col>
                </el-row>
            </el-col>
            <el-col :span="6" style="float: right;">
                <el-row style="text-align: right;">
                    <span class="share-convert">
                        <span class="iconfont icon-shoucang" >&nbsp;收藏</span> &nbsp;
                        <span class="iconfont icon-zhuanfa">&nbsp;转发</span>
                    </span>
                </el-row>
                <el-row class="background-teacher">
                    <el-col :span="4">
                        <img class="teacher-img" src="https://img-bss.csdnimg.cn/20220407205837850.jpg?imageMogr2/auto-orient/thumbnail/150x150!/format/jpg" alt="">
                    </el-col>
                    <el-col :span="6" >
                        <el-row class="teacher-name">吴思豪</el-row>
                        <div class="divider-right-name"></div>
                    </el-col>
                    <el-col :span="6" class="course-sum">
                        <el-row style="color: lightgreen;">44</el-row>
                        <el-row style="color: white;">课程数量</el-row>
                        <div class="divider-right"></div>
                    </el-col>
                    <el-col :span="6" class="course-sum">
                        <el-row style="color: lightgreen;">9544</el-row>
                        <el-row style="color: white;">学生人数</el-row>
                    </el-col>
                </el-row>
                <el-row>
                    <el-row class="teacher-profile">讲师简介</el-row>
                    <el-row class="teacher-profile-content">
                        擅长数据挖掘，量化交易
                    </el-row>
                </el-row>
            </el-col>
        </el-row>

        <el-row class="anchor-point">
        <a class="nav-anchor" @click="showDetails(1)">课程介绍<span class="underline"></span></a>
        <a class="nav-anchor" style="margin-left: 65px;">课程目录</a>
        <a class="nav-anchor" style="margin-left: 65px; ">讨论留言</a>
        </el-row>

        
        </el-row>
        <el-row class="content" style="padding: 30px;">
                <el-col  :span="18">
                    <el-card style="position: relative;" class="card">
                        <el-row class="will-harvest">你将收获</el-row>
                        <span class="background-content"></span>
                        <el-row>
                            <vue-markdown 
                            class="markdown-content"
                            :source="course.harvest" 
                            :highlight="true"
                            :html="true"
                            :xhtmlOut="true">
                            </vue-markdown>
                        </el-row>
                    </el-card>
                    <el-card style="margin-top: 10px;" class="card">
                        <el-row class="will-harvest">适用人群</el-row>
                        <span class="background-content"></span>
                        <el-row>
                            <vue-markdown 
                            class="markdown-content"
                            :source="course.harvest" 
                            :highlight="true"
                            :html="true"
                            :xhtmlOut="true">
                            </vue-markdown>
                        </el-row>
                    </el-card>

                    <el-card style="position: relative; margin-top: 10px;" class="card">
                        <el-row class="will-harvest">课程介绍</el-row>
                        <span class="background-content"></span>
                        <el-row>
                            <vue-markdown 
                            class="markdown-content"
                            :source="course.harvest" 
                            :highlight="true"
                            :html="true"
                            :xhtmlOut="true">
                            </vue-markdown>
                        </el-row>
                    </el-card>
                    <el-card style="position: relative; margin-top: 10px;" class="card">
                            <el-row class="will-harvest">课程目录</el-row>
                            <span class="background-content"></span>
                            <el-row>
                                <vue-markdown 
                                class="markdown-content"
                                :source="course.harvest" 
                                :highlight="true"
                                :html="true"
                                :xhtmlOut="true">
                                </vue-markdown>
                            </el-row>
                    </el-card>
           
                </el-col>
                <el-col :span="6"></el-col>
        </el-row>
    </div>
</template>

<script>
import VueMarkdown from 'vue-markdown';
export default {
    name: 'MonkeyWebCourseDetail',
    components: {
        VueMarkdown
    },
    data() {
        return {
            value: 3.7,
            course: {
                harvest: "零基础学会CTP下单查询等操作\n多线程稳定CTP交易系统\n期货的保证金，手续费，平仓盈亏等计算方法\n将期货量化策略付诸实践并从中获利"
            },
        };
    },

    methods: {
        // 跳转到对应的锚点
        showDetails(num) {
            var el = document.getElementById(`listshow${num}`);
            // 给指定的id元素加上特定类
            el.classList.add("selected");
            el.classList.remove("test");
            console.log(el.offsetTop);
            if (this.showIndex == num) {
                this.showIndex = null;
            } else {
                this.showIndex = num;
            }
            this.$nextTick(function () {
                window.scrollTo({ "behavior": "smooth", "top": el.offsetTop });
            })
        }

    },
};
</script>

<style scoped>
.card {
    border-radius: 20px;
    position: relative; 
}
.markdown-content {
    font-size: 14px;
    opacity: 0.8;
}
.background-content {
    position: absolute;
    background-color: #409EFF;
    width: 5px;
    height: 20px;
    top: 25px;
    left: 8px;
}
.will-harvest {
    top: 2px;
}
.content {
    margin: 10px auto; 
    width: 1200px;
}
.selected {
    color: red;
    background-color: red;
    width: 100%;
    height: 800px;
}
.underline {
    width: 70px;
    height: 1px;
    background-color: black;
    position: absolute;
    top: 25px;
    left: 0px;
}
.nav-anchor:hover {
    cursor: pointer;
    color: black;
    opacity: 1;
}
.nav-anchor {
    opacity: 0.5;
    position: relative;
}
.anchor-point {
    line-height: 60px;
    background: white;
    height: 60px;
    text-align: center;
}
.teacher-profile-content {
    color: white;
    font-size: 14px;
    padding-top: 10px;
}
.teacher-profile {
    color: white;
    padding-top: 10px;
    font-size: 14px;
}
.divider-right-name {
    position: absolute; 
    height: 25px; 
    width: 1px; 
    margin-left: 80px;
    top: 20px;
    background-color: white;
}
.divider-right {
    position: absolute; 
    height: 25px; 
    width: 1px; 
    margin-left: 80px;
    top: 20px;
    background-color: white;
}
.course-sum {
    margin-top: 10px;
}
.teacher-name {
    margin-top: 20px;
    color: white;
}
.teacher-sex {
    margin-top: 10px;
    color: white;
}
.background-teacher {
    text-align: center;
    padding: 0;
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: 15px;
}
.teacher-img {
    width: 55px; 
    height: 55px; 
    border-radius: 50%;
    padding-top: 3px;
}
.icon-zhuanfa:hover {
    cursor: pointer;
    color: #57c6e1;
}
.share-convert{
    color: white;
    font-size: 30px;
    padding: 5px;
}
.icon-shoucang:hover {
    cursor: pointer;
    color: #57c6e1;
}
.title {
    padding-left: 29px;
}
.original-price {
    margin-top: 12px;
    text-align: left;
    padding-left: 20px;
    color: #E5E9F0;
    text-decoration: line-through;
}
.discount {
    margin-top: 12px;
    text-align: center;
    color: white;
    height: 22px;
    font-size: 14px;
    padding-top: 1px;
    width: 110px;
    background-image: linear-gradient(to right, #eea2a2 0%, #bbc1bf 19%, #57c6e1 42%, #b49fda 79%, #7ac5d8 100%);
}
.buy-view::before {
    content: "￥";
}
.buy-view {
    height: 50px; 
    font-size: 20px; 
    font-weight: bolder;
    color: black;
}
.el-icon-view {
    font-size: 20px; 
    font-weight: bolder;
}
.free-view {
    width: 150px; 
    height: 50px; 
    font-size: 20px; 
    font-weight: bolder;
}

.divider-left {
    position: absolute; 
    height: 25px; 
    width: 1px; 
    margin-left: 115px;
    top: 30px;
    background-color: white;
}
.ellipsis-title {
    font-size: 27px;
    font-weight: bold;
    color: white;
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    text-align: left;
}

.top {
    background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px 40px;
    width: 1380px;
    margin: 0 auto;
  

}
.all-background {
    background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    margin: 0 auto;
    height: 100%;
}
</style>