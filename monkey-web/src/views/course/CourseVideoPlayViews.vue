<template>
    <div class="MonkeyWebCourseVideoPlayViews-container">
        <el-row style="padding: 20px; background-color: #FFFFFF;;">
            <el-col :span="16">
                <el-row class="course-title">
                    尚硅谷前端html+css零基础教程，2023最新前端开发html5+css3视频
                </el-row>
                <el-row>
                    <span class="el-icon-video-play base-info">&nbsp;45</span>
                    &nbsp;
                    <span class="iconfont icon-danmu base-info">&nbsp;12</span>
                    &nbsp;
                    <span class="base-info">2023-03-07 14:55:00</span>
                </el-row>
            
                <CourseVideo/>
                <el-row class="video-bottom">
                    <el-col :span="2">
                        <el-tooltip class="item" effect="dark" content="收藏" placement="top">
                            <span class="el-icon-star-on" style="font-size: 30px; cursor: pointer;"></span>
                        </el-tooltip>
                        
                    </el-col>
                    

                    <el-col :span="2">
                        
                        <el-tooltip class="item" effect="dark" content="打开弹幕" placement="top" >
                            <span 
                            class="iconfont 
                            icon-danmukaiqi 
                            outer-barrage-button
                            space
                            " 
                            style="cursor: pointer;"
                            v-show="!isOpenBarrage" 
                            @click.stop ="isOpenBarrage = !isOpenBarrage"
                            @mouseover.stop="isHoverBarrageButton = true"
                            @mouseleave.stop="isHoverBarrageButton = false">
                            </span>
                        </el-tooltip>
                        

                        <el-tooltip class="item" effect="dark" content="关闭弹幕" placement="top" >
                            <span 
                            class="iconfont 
                            space
                            icon-danmuguanbi
                            outer-barrage-button
                            " 
                            style="cursor: pointer;"
                            v-show="isOpenBarrage" 
                            @click.stop ="isOpenBarrage = !isOpenBarrage"
                            @mouseover.stop="isHoverBarrageButton = true"
                            @mouseleave.stop="isHoverBarrageButton = false">
                            </span>
                        </el-tooltip>
                        
                    </el-col>
                    
                    <el-col :span="20" style="display: flex; justify-content: right; align-items: center;">
                        <el-color-picker class="space" v-model="selectSendBarrageColor" show-alpha></el-color-picker>
                    
                    <el-input  
                        @keydown.native="handleKeyDownBarrage($event)"
                        placeholder="发个友善的弹幕见证当下" 
                        class="outer-barrage-content" 
                        v-model="barrageContent">
                    </el-input>
                    <el-button  class="outer-input-button" type="primary" @click="sendBarrage()">
                        发送
                    </el-button>
                    </el-col>
                    
                </el-row>

                <el-row class="divisor"></el-row>
                
                <el-row>
                    <el-row >
                        <el-col :span="12" class="user-evaluate">
                            用户评价
                        </el-col>
                        <el-col :span="12" class="user-evaluate-right">
                            <span class="el-icon-arrow-left user-evaluate-arrow"></span>
                           
                             <span style="padding-bottom: 2px;">1</span>
                            <span class="el-icon-arrow-right user-evaluate-arrow"></span>
                        </el-col>
                    </el-row>
                </el-row>
                
            </el-col>
            <el-col :span="8">
                 
            </el-col>
        </el-row>
    </div>
</template>

<script>


import CourseVideo from '@/components/course/CourseVideo'
export default {
    name: 'MonkeyWebCourseVideoPlayViews',
    components: {
        CourseVideo
    },
    data() {
        return {
        // 选择发送的弹幕颜色
        selectSendBarrageColor: 'rgba(19, 206, 102, 0.8)',
        // 鼠标是否悬浮在弹幕按钮上
        isHoverBarrageButton: false,
        // 是否开启了弹幕，true表示已开启，false表示未开启
        isOpenBarrage: false,
        // 输入的弹幕内容
        barrageContent: '',
	}
    },
    


    methods: {
        handleKeyDownBarrage(e) {
            if (e.keyCode === 13 && !e.ctrlKey) {
                // Enter，换行

                this.content += '\n';
            } else if (e.keyCode === 13 && e.ctrlKey) {
                // Ctrl + Enter，发送消息
                this.publishCourseComment(this.content, this.courseId);
            }
        },
    },

    
}
</script>

<style scoped >
.user-evaluate-arrow {
    font-size: 35px; 
    cursor: pointer;
    color: gray;
}
.user-evaluate-arrow:hover {
    color: black;
}
.user-evaluate-right {
    padding-top: 5px;
    display: flex;
    justify-content: right;
    font-size: 30px;
    color: gray;
}
.divisor {
    background-color: gray;
    opacity: 0.3;
    height: 1px;
    margin: 20px 0px;
}
.user-evaluate {
    font-size: 30px;
    font-weight: 600;
}
 ::v-deep .el-rate__icon{
  font-size: 30px
}

.space {
    margin: 0px 10px;
}
.video-bottom {
    display: flex; 
    align-items: center;
    padding: 20px 0px 0px 0px; 
    
}
.outer-barrage-content {
    width: 40%;
}
.outer-barrage-button {
    font-size: 30px;
}


.course-title {
    font-size: 20px;
}
.base-info {
    font-size: 14px;
    opacity: 0.7;
    margin-top: 10px;
}
.MonkeyWebCourseVideoPlayViews-container {
    background-color: #FFFFFF;
    width: 80vw;
    height: 300px;
    margin: 10px auto;
}

</style>