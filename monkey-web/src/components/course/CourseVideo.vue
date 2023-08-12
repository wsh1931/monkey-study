<template>
    <div>
        <div id="mui-player" class="player-container" style="margin-top: 20px;">
                <div class="danmaku-container">
                    <vue-danmaku
                    style=""
                    :randomChannel="true"
                    :danmus="danmus"
                    :fontSize="24"
                    :channels="channels"
                    loop
                    class="danmakuClass"
                    ref="danmaku"
                    @play-end="end">
                    </vue-danmaku>
            </div>

                <span 
                class="iconfont 
                icon-danmushezhi 
                danmaku-setting-class" 
                v-if="isFullScreen && isShow" 
                @click.stop>
            </span>

                <!-- 弹幕按钮 -->
                <el-row class="danmaku">

                    <span
                    class="barrage-tips" 
                    v-if="isHoverBarrageButton && isOpenBarrage && isFullScreen" 
                    >打开弹幕
                    </span>

                    <span 
                    class="barrage-tips" 
                    v-if="isHoverBarrageButton && !isOpenBarrage && isFullScreen" 
                    >关闭弹幕</span>
                    <span 
                    class="iconfont 
                    icon-danmukaiqi 
                    danmaku-class
                    
                    " 
                    v-show="isFullScreen && isShow && !isOpenBarrage" 
                    @click.stop ="isOpenBarrage = !isOpenBarrage"
                    @mouseover.stop="isHoverBarrageButton = true"
                    @mouseleave.stop="isHoverBarrageButton = false">
                    </span>


                    <span 
                    class="iconfont 
                    icon-danmuguanbi
                    danmaku-class" 
                    v-show="isFullScreen && isShow && isOpenBarrage" 
                    @click.stop ="isOpenBarrage = !isOpenBarrage"
                    @mouseover.stop="isHoverBarrageButton = true"
                    @mouseleave.stop="isHoverBarrageButton = false">
                    </span>
                </el-row>

                <div class="barrage-content-parent" v-if="isFullScreen && isShow" @click.stop>
                    <el-input  
                    placeholder="发个友善的弹幕见证当下" 
                    class="barrage-content" 
                    v-model="barrageContent">    
                    </el-input>
                    <el-button  class="input-button" type="primary" @click="sendBarrage()">
                        发送
                    </el-button>
                </div>
            </div>
    </div>
</template>

<script>
import MuiPlayer from 'mui-player'
import vueDanmaku from 'vue-danmaku'
// 使用模块管理器引入插件
import MuiPlayerDesktopPlugin from 'mui-player-desktop-plugin'
export default {
    name: 'MonkeyWebCourseVideo',
    components: {
        vueDanmaku
    },
    data() {
        return {
            // 鼠标是否悬浮在弹幕按钮上
            isHoverBarrageButton: false,
            // 是否开启了弹幕，true表示已开启，false表示未开启
            isOpenBarrage: false,
            // 输入的弹幕内容
            barrageContent: '',
            // 判断目前播放器是否是全屏
            isFullScreen: false,
            // 当前屏幕组件是否显示
            isShow: false,
            mulplayer: {},
            channels: 20,
            barrageList: [],
            barrageIsShow: true,    	//是否展示弹幕
            barrageLoop: true,     	//是否循环播放
            boxHeight: 412,         	//高度
            messageHeight: 25,		//消息高度
            lanesCount: 4,			//泳道数量
            throttleGap: 5000,		//消息间隔
            video: {},
            danmus: ['danmu1', 'danmu2', 'danmu3', '...'],
            // 弹幕样式
        };
    },

    mounted() {
        this.mulplayer = new MuiPlayer({
            container: '#mui-player',
            title: '花果山博客测试',
            themeColor: '#409EFF',
            webpage: true,
            preload: "auto",
            showMiniProgress: false,
            dragSpotShape: 'circular',
            poster: 'https://monkey-blog.oss-cn-beijing.aliyuncs.com/article/relax.jpg',
            src: "https://outin-bf44d75f6c7011ed8f3800163e1a625e.oss-cn-shanghai.aliyuncs.com/sv/629b5544-184b3128e50/629b5544-184b3128e50.mp4?Expires=1691828143&OSSAccessKeyId=LTAIVVfYx6D0HeL2&Signature=C0lWy4nJDKMaGZ69VRG46ebS1DE%3D",
            plugins: [
                new MuiPlayerDesktopPlugin({
                    thumbnails: true,
                    leaveHiddenControls: true,
                    customSetting: [
                    {
                        functions:'清晰度',
                        model:'select',
                        show:true,
                        zIndex:0,
                        childConfig:[
                            {functions:'蓝光1080P'},
                            {functions:'超清'},
                            {functions:'高清',selected:true},
                            {functions:'标清'},
                        ],
                        onToggle:function(data,selected,back) {
                            // Action
                            console.log(data);
                            console.log(selected)
                            console.log(back)
                        }
                    }
                        
                    ],
                    contextmenu:[
                    {
                        name:'copyPlayUrl',
                        context:'复制视频链接',
                        zIndex:0,
                        show:true,
                        click:function(close) {
                            // Action...
                            console.log(close);
                        }
                    },
                ],
                })
            ],
            custom:{
                footerControls:[
                {
                    slot:'barrage', // 对应定义的 slot 值
                    position:'left', // 显示的位置，可选 left、right
                    tooltip:'下一集', // 鼠标悬浮在控件上显示的文字提示
                    oftenShow:true, // 是否常显示。默认为false，在 mobile 环境下竖屏状态下隐藏，pc环境判下视频容器小于500px时隐藏
                    click:function(e) { // 按钮点击事件回调
                        console.log('next media button click...', e);
                    },
                style: {
                
                    }, // 自定义添加控件样式
                },
                {
                    slot: "send-barrage",
                    position: 'right',
                    oftenShow: true,
                    tooltip: "选择弹幕发送的颜色",
                    click:function(e) { // 按钮点击事件回调
                        console.log('next media button click...', e);
                    },
                    style: {
                        width: '100px'
                    },
                }
            ],
            }
        })
        this.video = this.mulplayer.video()
        this.init();

        this.mulplayer.on('controls-toggle', (data) => {
            this.isShow = data.show;
        })

        // 点击网页全屏触发的事件
        this.mulplayer.on('pagefull-change', data => {
            if (data.pagefull) {
                // 如果当前是全屏设置弹幕宽度
                if (this.$refs['danmaku'] != null) {
                    this.$refs['danmaku'].resize();
                }
                this.isFullScreen = true;
            } else {
                this.isFullScreen = false;
            }
        })

        // 点击视频全屏触发的事件
        this.mulplayer.on('fullscreen-change', (data) => {
            if (data.fullscreen) {
                // 如果当前是全屏设置弹幕宽度
                if (this.$refs['danmaku'] != null) {
                    this.$refs['danmaku'].resize();
                }
                this.isFullScreen = true;
            } else {
                this.isFullScreen = false;
            }
        });
        
    },

    methods: {
        init() {
            const vue = this;
            this.video.addEventListener('timeupdate', function() {
        // 视频时间变化时触发的代码逻辑
            console.log(vue.video.currentTime)
        });
            // let previousTime = 0
            // setInterval(function() {
            //     const currentTime = vue.video.currentTime;

            //     if (currentTime != previousTime) {
            //         // 视频时间变化时触发的代码逻辑
            //         console.log('视频时间变化了');
            //         console.log(currentTime);
            //         previousTime = currentTime;
            //     }
            // }, 5); // 每隔500毫秒检查一次视频时间

            // console.log("aaa");
        },
        sendBarrage() {
            console.log("发送弹幕测试")
        },
        mousemove() {
            console.log("鼠标移出")
        },
    },
    unmounted() {
        
        this.mulplayer.on('destroy', () => {
            alert("already-destroy")
        })
    },
    pause() {
        console.log(this.$refs["danmaku"].getPlayState())
    },
    end() {
        console.log(this.$refs.danmaku);
    },
    handleKeyDownBarrage(e) {
            if (e.keyCode === 13 && !e.ctrlKey) {
                // Enter，换行

                this.content += '\n';
            } else if (e.keyCode === 13 && e.ctrlKey) {
                // Ctrl + Enter，发送消息
                this.publishCourseComment(this.content, this.courseId);
            }
        },
};
</script>

<style scoped>
.danmaku-item {
  position: absolute;
  white-space: nowrap;
  font-size: 14px;
  color: #fff;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

/* 弹幕背景样式 */
.danmaku-item::before {
  content: "";
  display: inline-block;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  border-radius: 4px;
}
.input-button {
    background-color: #00AEEC;
    color: black;
    font-weight: 600;
    left: 300px;
    top: 0px;
    animation: barrage-content-parent 0.15s linear;
    margin-left: -2px;
    border-radius: 0px 10px 10px 0px;
    position: absolute;
}

@keyframes barrage-content-parent {
    0% {
        opacity: 0;
        transform: translateY(50px);
    }
    100% {
        opacity: 1;
        transform: translateY(0px);
    }
}

.barrage-content {
    width: 70%;
    animation: barrage-content-parent 0.15s linear;
}
.barrage-content-parent {
    animation: barrage-content-parent 0.22s linear;
    position: absolute;
    top: 94.3%;
    left: 500px;
    z-index: 20000;
    width: 30%;
}
@keyframes take-in {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}
.barrage-tips {
    position: absolute;
    background-color: black;
    opacity: 0.9;
    color: white;
    top: -30px;
    left: -30px;
    width: 100px;
    text-align: center;
    animation: take-in 0.4s linear;
}
.danmaku {
    position: absolute;
    top: 95%;
    left: 420px;
    z-index: 20000;
    animation: barrage-content-parent 0.22s linear;
}
@keyframes barrage-content-parent {
    0% {
        opacity: 0;
        transform: translateY(50px);
    }
    100% {
        opacity: 1;
        transform: translateY(0px);
    }
}
.danmaku-setting-class {
    color: white;
    position: absolute;
    top: 95%;
    left: 460px;
    z-index: 20000;
    font-size: 30px;
    cursor: pointer;
    animation: barrage-content-parent 0.22s linear;
}
.player-container {
    position: relative;
    z-index: 0;
}

.danmaku-container {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 1;
}

.danmakuClass {
    width: 100%;
    height: 100%;
}

.danmaku-class {
    color: white;
    font-size: 30px;
    cursor: pointer;
}
</style>