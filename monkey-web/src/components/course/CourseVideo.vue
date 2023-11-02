<template>
    <div>
        <ReportContent
        v-if="showReportContent"
        @reportContent="reportContent"
        :reportContentType="reportContentType"
        :reportContentAssociationId="reportContentAssociationId"
        @closeReportContent="closeReportContent"/>
        <CollectCard v-if="showCollect"
            :associateId="associateId"
            :showCollect="showCollect"
            :collectType="collectType"
            :collectTitle="collectTitle"
            @closeCollect="closeCollect"/>
        <div id="mui-player" class="player-container" style="margin-top: 20px;">
                <div class="danmaku-container"
                @mouseenter="mouseEnter()">
                    <vue-danmaku
                    style="color: red;"
                    :randomChannel="true"
                    :danmus="barrageList"
                    :fontSize="barrageFontSize"
                    :channels="channels"
                    :speeds="barrageSpeed"
                    :loop="barrageLoop"
                    class="danmakuClass"
                    ref="barrage">
                    </vue-danmaku>
                </div>

                <!-- 弹幕按钮 -->
                <el-row class="danmaku">

                    <span
                    class="barrage-tips" 
                    v-if="isHoverBarrageButton && isOpenBarrage && isFullScreen" 
                    >关闭弹幕
                    </span>

                    <span 
                    class="barrage-tips" 
                    v-if="isHoverBarrageButton && !isOpenBarrage && isFullScreen" 
                    >打开弹幕
                    </span>
                    <span 
                    class="iconfont 
                    icon-danmukaiqi 
                    danmaku-class
                    " 
                    v-show="isFullScreen && isShow && isOpenBarrage" 
                    @click.stop ="closeBarrage()"
                    @mouseover.stop="isHoverBarrageButton = true"
                    @mouseleave.stop="isHoverBarrageButton = false">
                    </span>

                    
                    <span 
                    class="iconfont 
                    icon-danmuguanbi
                    danmaku-class" 
                    v-show="isFullScreen && isShow && !isOpenBarrage" 
                    @click.stop ="openBarrage()"
                    @mouseover.stop="isHoverBarrageButton = true"
                    @mouseleave.stop="isHoverBarrageButton = false">
                    </span>

                <!-- 弹幕设置 -->
                <div
                @click.stop
                class="barrage-setting-tips" 
                @mouseover.stop="isHoverBarrageSetting = true, isHoverBarrageSettingBorder = true"
                @mouseleave.stop="isHoverBarrageSetting = false, isHoverBarrageSettingBorder = false"
                v-if="isFullScreen && isHoverBarrageSetting && isShow" 
                >
                <el-switch
                style="margin-right: 10px;"
                v-model="barrageSetting.synonymPlaySpeed"
                active-color="#13ce66"
                inactive-color="#ff4949">
                </el-switch>
                <span>弹幕速度同步播放倍数</span>

                <div class="block">
                    <span class="no-transparent">不透明度</span>
                    <el-slider v-model="barrageSetting.noTransPrant"></el-slider>
                    <span class="no-transparent-number">{{ barrageSetting.noTransPrant }}</span>
                </div>


                <div class="block">
                    <span class="show-area">显示区域</span>
                    <el-slider
                    :step="25"
                    :min="25"
                    v-model="barrageSetting.showArea"
                    :marks="barrageSetting.showAreaSign">
                    </el-slider>
                    <span class="show-area-number" >{{ barrageSetting.showArea | formatShowArea }}</span>
                </div>


                <div class="block">
                    <span class="barrage-font-size">弹幕字号</span>
                    <el-slider v-model="barrageSetting.barrageFontSize"></el-slider>
                    <span class="barrage-font-size-number">{{ barrageSetting.barrageFontSize }}%</span>
                </div>


                <div class="block">
                    <span class="barrage-speed">弹幕速度</span>
                    <el-slider
                    :step="25"
                    v-model="barrageSetting.barrageSpeed"
                    :marks="barrageSetting.barrageSpeedSign">
                    </el-slider>
                    <span class="barrage-speed-number">{{ barrageSetting.barrageSpeed | formBarrageSpeed }}</span>
                </div>

                </div>
                <span 
                class="iconfont 
                icon-danmushezhi 
                danmaku-setting-class" 
                @mouseover.stop="isHoverBarrageSetting = true"
                @mouseleave.stop="secondsAfter()"
                v-if="isFullScreen && isShow">
                </span>
                </el-row>

                <div class="barrage-content-parent" v-if="isFullScreen && isShow" @click.stop>
                    <el-input  
                    @blur="videoCloseTime()"
                    @focus="videoShowTime()"
                    @keyup.native="handleKeyDownBarrage($event)"
                    placeholder="发个友善的弹幕见证当下" 
                    class="barrage-content" 
                    v-model="barrageContent">    
                    </el-input>
                    <el-button  class="input-button" type="primary" @click="sendBarrage(barrageContent)">
                        发送
                    </el-button>
                </div>
            </div>

            <!-- 下方 -->
            <el-row class="video-bottom">
                    <el-col :span="2">
                        <el-tooltip class="item" effect="dark" content="收藏" placement="top">
                            <span 
                            v-if="isCollect == '0'"
                        @click="userCollect(courseInfo.id, courseInfo.title)" 
                        class="el-icon-star-on" style="font-size: 24px; cursor: pointer;">&nbsp;{{ courseInfo.collectCount }}</span>
                        <span 
                        v-if="isCollect == '1'"
                        @click="userCollect(courseInfo.id, courseInfo.title)" 
                        class="el-icon-star-on" style="font-size: 24px; cursor: pointer; color: lightgreen;">&nbsp;{{ courseInfo.collectCount }}</span>
                        </el-tooltip>
                        
                    </el-col>
                    
                    <el-col :span="2">
                        <el-tooltip class="item" effect="dark" content="转发" placement="top">
                        <span 
                        class="iconfont icon-zhuanfa convert" >&nbsp;&nbsp;2
                        </span>
                        </el-tooltip>

                    </el-col>
                    <el-col :span="2">
                        <span 
                        @click="reportContent(courseInfo.id)"
                        class="el-icon-warning"
                        style="font-size: 20px; cursor: pointer;">&nbsp;举报</span>
                    </el-col>
                    
                    <el-col :span="18" style="display: flex; justify-content: right; align-items: center;">
                        <el-tooltip class="item" effect="dark" content="关闭弹幕" placement="top" >
                            <span 
                            class="iconfont 
                            icon-danmukaiqi 
                            outer-barrage-button
                            space
                            " 
                            style="cursor: pointer;"
                            v-show="isOpenBarrage" 
                            @click.stop ="closeBarrage()"
                            @mouseover.stop="isHoverBarrageButton = true"
                            @mouseleave.stop="isHoverBarrageButton = false">
                            </span>
                        </el-tooltip>
                        

                        <el-tooltip class="item" effect="dark" content="开启弹幕" placement="top" >
                            <span 
                            class="iconfont 
                            space
                            icon-danmuguanbi
                            outer-barrage-button
                            " 
                            style="cursor: pointer;"
                            v-show="!isOpenBarrage" 
                            @click.stop ="openBarrage()"
                            @mouseover.stop="isHoverBarrageButton = true"
                            @mouseleave.stop="isHoverBarrageButton = false">
                            </span>
                        </el-tooltip>
                        <!-- 弹幕设置 -->
                    <div
                        @click.stop
                        class="outer-barrage-setting-tips" 
                        @mouseover.stop="isHoverBarrageSetting = true, isHoverBarrageSettingBorder = true"
                        @mouseleave.stop="isHoverBarrageSetting = false, isHoverBarrageSettingBorder = false"
                        v-if="isHoverBarrageSetting" 
                        >
                        <el-switch
                        style="margin-right: 10px;"
                        v-model="barrageSetting.synonymPlaySpeed"
                        active-color="#13ce66"
                        inactive-color="#ff4949">
                        </el-switch>
                        <span>弹幕速度同步播放倍数</span>

                        <div class="block">
                            <span class="outer-no-transparent">不透明度</span>
                            <el-slider v-model="barrageSetting.noTransPrant"></el-slider>
                            <span class="outer-no-transparent-number">{{ barrageSetting.noTransPrant }}</span>
                        </div>


                        <div class="block">
                            <span class="outer-show-area">显示区域</span>
                            <el-slider
                            :step="25"
                            :min="25"
                            v-model="barrageSetting.showArea"
                            :marks="barrageSetting.showAreaSign">
                            </el-slider>
                            <span class="outer-show-area-number" >{{ barrageSetting.showArea | formatShowArea }}</span>
                        </div>


                        <div class="block">
                            <span class="outer-barrage-font-size">弹幕字号</span>
                            <el-slider v-model="barrageSetting.barrageFontSize"></el-slider>
                            <span class="outer-barrage-font-size-number">{{ barrageSetting.barrageFontSize }}%</span>
                        </div>


                        <div class="block">
                            <span class="outer-barrage-speed">弹幕速度</span>
                            <el-slider
                            :step="25"
                            v-model="barrageSetting.barrageSpeed"
                            :marks="barrageSetting.barrageSpeedSign">
                            </el-slider>
                            <span class="outer-barrage-speed-number">{{ barrageSetting.barrageSpeed | formBarrageSpeed }}</span>
                        </div>

                    </div>
                    <span 
                    class="iconfont 
                    icon-danmushezhi 
                    outer-danmaku-setting-class" 
                    @mouseover.stop="isHoverBarrageSetting = true"
                    @mouseleave.stop="secondsAfter()">
                    </span>

                    <el-tooltip class="item" effect="dark" content="选择弹幕颜色" placement="top">
                        <el-color-picker class="space" v-model="selectSendBarrageColor" show-alpha></el-color-picker>
                    </el-tooltip>
                        
                        
                        <el-input
                        @focus="videoShowTime()"
                        @blur="videoCloseTime()"
                            @keyup.native="handleKeyDownBarrage($event)"
                            placeholder="发个友善的弹幕见证当下" 
                            class="outer-barrage-content" 
                            v-model="barrageContent">
                        </el-input>
                        <el-button  class="outer-input-button" type="primary" @click="sendBarrage(barrageContent)">
                            发送
                        </el-button>
                    </el-col>
                </el-row>
    </div>
</template>

<script>
import $ from "jquery"
import MuiPlayer from 'mui-player'
import vueDanmaku from 'vue-danmaku'
import store from '@/store';
// 使用模块管理器引入插件
import MuiPlayerDesktopPlugin from 'mui-player-desktop-plugin'
import WebSocketServer from '@/socket/WebSocketServer';
import CourseComment from "./CourseComment.vue";
import CollectCard from "../collect/CollectCard.vue";
import ReportContent from '@/components/report/ReportContent'
export default {
    name: 'MonkeyWebCourseVideo',
    components: {
        vueDanmaku,
        CourseComment,
        CollectCard,
        ReportContent
    },
    props: ['videoInfo', 'courseInfo', 'barrageListTotal'],
    data() {
        return {
            // 举报类型(0表示文章，1表示问答，2表示课程, 3表示社区，4表示社区文章, 5表示资源)
            reportContentType: this.reportContentType.course,
            // 举报关联id
            reportContentAssociationId: "0",
            // 显示举报内容框
            showReportContent: false,
            // 课程id
            courseId: "",
            // 当前登录用户是否收藏该课程
            isCollect: "0",
            // 是否展示收藏夹
            showCollect: false,
             // 收藏类型
            collectType: 2,
            // 收藏标题
            collectTitle: "",
            // 收藏关联id
            associateId: "",
            webSocket: "",
            socketUrl: `wss://localhost:80/websocket/barrage/${store.state.user.token}`,
            // 弹幕速度
            barrageSpeed: 200,
            // 弹幕字号大小
            barrageFontSize: 22,
            // 选择发送的弹幕颜色
            selectSendBarrageColor: 'rgba(215, 215, 215, 1)',
            // 弹幕设置组件
            barrageSetting: {
                // 是否开启弹幕速度同步播放倍数, 0表示不开启，1表示开启
                synonymPlaySpeed: true,
                // 不透明度
                noTransPrant: 100,
                // 显示区域
                showArea: 50,
                // 显示区域的标记
                showAreaSign: {
                    25: "1/4",
                    50: "1/2",
                    75: "3/4",
                    100: '1'
                },
                // 弹幕字号
                barrageFontSize: 50,
                // 弹幕速度
                barrageSpeed: 50,
                // 弹幕速度标记
                barrageSpeedSign: {
                    0: "慢",
                    25: "较慢",
                    50: "适中",
                    75: "较快",
                    100: "快"
                },
                // 是否悬浮在弹幕框上
                isHoverBarrageSettingBorder: false,
            },
            // 鼠标是否悬浮在弹幕设置上
            isHoverBarrageSetting: false,
            // 鼠标是否悬浮在弹幕按钮上
            isHoverBarrageButton: false,
            // 是否开启了弹幕，true表示已开启，false表示未开启
            isOpenBarrage: true,
            // 输入的弹幕内容
            barrageContent: '',
            // 判断目前播放器是否是全屏
            isFullScreen: false,
            // 当前屏幕组件是否显示
            isShow: false,
            mulplayer: {},
            channels: 7,
            barrageIsShow: true,    	//是否展示弹幕
            barrageLoop: false,     	//是否循环播放
            boxHeight: 412,         	//高度
            messageHeight: 25,		//消息高度
            lanesCount: 4,			//泳道数量
            throttleGap: 5000,		//消息间隔
            video: {},
            barrageList: [],
            // 暂时变量，防止用户拖动进度条弹幕全部删除
            tempBarrageList: [],
            // 课程播放url
            coursePlayUrl: "http://localhost/monkey-course/video/player",
            // 课程详细信息地址
            courseDetailUrl: "http://localhost/monkey-course/detail",
        };
    },
    filters: {
    // 格式化显示弹幕速度
    formBarrageSpeed(value) {
        if (value == 0) {
            return "慢";
        } else if (value == 25) {
            return "较慢";
        } else if (value == 50) {
            return "适中"
        } else if (value == 75) { 
            return "较快"
        } else if (value == 100) {
            return "快"
        }
    },
        // 格式话显示区域
        formatShowArea(value) {
            if (value == 25) {
                return "1/4";
            } else if (value == 50) {
                return "半屏"
            } else if (value == 75) {
                return "3/4"
            } else if (value == 100) {
                return "全屏"
            }
        }
    },

    created() {
        this.courseId = this.$route.params.courseId;
        this.judgeIsCollect(this.courseId)
        this.getBarrageListByCourseVideoId(this.videoInfo.id)
    },
    mounted() {

        // 初始化视频播放器
        this.initMuiplayer(this.videoInfo);

        // 初始化视频播放器监听事件
        this.initMuiplayerListenEvent();

        // 监听弹幕不透明度，当不透明度数值发生变化时执行该方法
        this.$watch('barrageSetting.noTransPrant', this.noTransPrantChange);

        // 当显示弹幕显示区域改变时触发此事件
        this.$watch('barrageSetting.showArea', this.changeShowArea);

        // 当弹幕字号改变时触发
        this.$watch("barrageSetting.barrageFontSize", this.changeBarrageFontSize);

        // 当弹幕速度改变时触发
        this.$watch("barrageSetting.barrageSpeed", this.changeBarrageSpeed);
        this.mulplayer.toggleControls(false).openTimer(32132132132);
        this.initWebSocket();
    },

    methods: {
        closeReportContent() {
            this.showReportContent = false;
        },
        reportContent(resourceId) {
            this.showReportContent = true;
            this.reportContentAssociationId = resourceId;
        },
        // 通过课程id得到课程基本信息
        getCourseInfoByCourseId(courseId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/getCourseInfoByCourseId",
                type: "get",
                data: {
                    courseId
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.courseInfo = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        judgeIsCollect(courseId) {
            const vue = this;
                $.ajax({
                    url: vue.courseDetailUrl + "/judgeIsCollect",
                    type: "get",
                    headers: {
                        Authorization: "Bearer " + store.state.user.token,
                    },
                    data: {
                        courseId,
                        // 在后端接
                        collectType: vue.collectType,
                    },
                    success(response) {
                        if (response.code == vue.ResultStatus.SUCCESS) {
                            vue.$emit("getCourseInfoByCourseId", courseId);
                            vue.isCollect = response.data;
                        } else {
                            vue.$modal.msgError(response.msg);
                    }
                    },
                })
            },
        // 用户收藏课程
        userCollect(courseId, title) {
            this.associateId = courseId;
            this.showCollect = true;
            this.collectTitle = title;
        },
        closeCollect(status) {
            this.showCollect = status;
            this.judgeIsCollect(this.courseInfo.id);
        },
        // 当播放去失去聚焦时，空间消失
        videoCloseTime() {
            this.mulplayer.toggleControls(false).openTimer(1000);
        },
        // 当播放器聚焦时，控件框显示时间
        videoShowTime() {
            this.mulplayer.toggleControls(true).closeTimer(24 * 1000 * 3600)
            
        },
        mouseEnter() {
        },
        // 通过视频id得到弹幕列表, 并判断该弹幕是否时自己发送的
        getBarrageListByCourseVideoId(courseVideoId) {
            const vue = this;
            $.ajax({
                url: vue.coursePlayUrl + "/getBarrageListByCourseVideoId",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                data: {
                    courseVideoId,
                },
                success(response) {
                    if (response.code == vue.ResultStatus.SUCCESS) {
                        vue.tempBarrageList = response.data;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 发送弹幕
        sendBarrage(barrageContent) {
            if (barrageContent == "" || barrageContent == null) {
                this.$modal.msgError("发送的弹幕不能为空")
                return ;
            }
            WebSocketServer.send({
                event: "send_barrage",
                    currentTime: this.video.currentTime.toString(),
                    barrageContent,
                    courseVideoId: this.videoInfo.id.toString(),
                    barrageColor: this.selectSendBarrageColor,
            })

            this.barrageContent = "";
        },
        // 发送按下键盘发送弹幕
        handleKeyDownBarrage(event) {
            if (event.keyCode == '13') {
                this.sendBarrage(this.barrageContent)
            }
        },
        // 初始化WebSocket
        initWebSocket() {
            WebSocketServer.connect(this.socketUrl);

            // 当前端接收到后端返回的信息时
            WebSocketServer.onMessage(message => {
                const event = message.event;
                if (event == "send_barrage") {
                    // 接收后端传过来的弹幕
                    const courseVideoBarrage = message.courseVideoBarrage;
                    // 加入弹幕列表, 返回下标
                    const barrageIndex = this.$refs["barrage"].add(courseVideoBarrage.content);
                    this.barrageListTotal.unshift(courseVideoBarrage);

                    // 获取具有自定义属性data-index的DOM元素, setTimeout是防止未加载进dom元素
                    setTimeout(() => {
                        // 获取 class 为 testfa 的 div 元素
                        var testfaDiv = document.querySelector(".danmakuClass");
                        let elements = testfaDiv.querySelector(`[data-index = '${barrageIndex}']`);
                        elements.style.color = courseVideoBarrage.barrageColor;
                        elements.style.border = "solid 1px #FFFFFF"
                        elements.style.padding = "5px"
                    }, 200)
                }
            })
        },
         // 当弹幕速度改变时触发
        changeBarrageSpeed(newValue, oldValue) {
            if (newValue == '0') {
                this.barrageSpeed = 100;
            } else if (newValue == '25') {
                this.barrageSpeed = 150;
            } else if (newValue == '50') {
                this.barrageSpeed = 200;
            } else if (newValue == '75') {
                this.barrageSpeed = 300;
            } else if (newValue == '100') {
                this.barrageSpeed = 400;
            }

        },
        // 当弹幕字号改变时触发
        changeBarrageFontSize(newValue, oldValue) {
            this.barrageFontSize = newValue * 0.4;
        },
        // 当显示区域改变时
        changeShowArea(newValue, oldValue) {
            if (newValue == '25') {
                this.channels = 7;
            } else if (newValue == '50') {
                this.channels = 14
            } else if (newValue == '75') {
                this.channels = 21
            } else if (newValue == '100') {
                this.channels = 30
            }
        },
        // 当不透明度发生变化时执行此方法
        noTransPrantChange(newValue, oldValue) {
            const danmakuClassChild = document.querySelector('.danmakuClass:nth-child(n)');
            danmakuClassChild.style.opacity = `${newValue * 0.01}`;
        },
        // 开启弹幕
        openBarrage() {
            this.isOpenBarrage = true;
            this.$refs['barrage'].show();
        },
        // 关闭弹幕
        closeBarrage() {
            this.isOpenBarrage = false;
            this.$refs["barrage"].hide();
        },
        addBarrage() {
        },
        secondsAfter() {
            setTimeout(() => {
                if (!this.isHoverBarrageSettingBorder) {
                    this.isHoverBarrageSetting = false
                }
                
            }, 100)
        },
        initMuiplayer(video) {
            const vue = this;
            this.mulplayer = new MuiPlayer({
            container: '#mui-player',
            title: video.title,
            themeColor: '#409EFF',
            webpage: true,
            preload: "auto",
            showMiniProgress: false,
            dragSpotShape: 'circular',
            src: video.courseVideoUrl,
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
                        }
                    }
                        
                    ],
                //     contextmenu:[
                //     {
                //         name:'copyPlayUrl',
                //         context:'复制视频链接',
                //         zIndex:0,
                //         show:true,
                //         click:function(close) {
                //             // Action...
                //         }
                //     },
                // ],
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
                    },
                    style: {
                        width: '100px'
                    },
                }
            ],
            }
            })
        },
        initMuiplayerListenEvent() {
            const vue = this;
            this.video = this.mulplayer.video()
            this.video.addEventListener('timeupdate', function () {
            // 视频时间变化时触发的代码逻辑
                let currentTime = vue.video.currentTime;
                for (let i = 0; i < vue.tempBarrageList.length; i++) {
                    if (currentTime >= vue.tempBarrageList[i].courseVideoTime) {
                        let barrage = vue.tempBarrageList[i];
                        let barrageColor = barrage.barrageColor
                        let barrageContent = barrage.content
                        vue.barrageList.push(`<div style='color: ${barrageColor};'>${barrageContent}</div>`);

                        vue.tempBarrageList.splice(i, 1);
                    }
                }
        });

            this.mulplayer.on('controls-toggle', (data) => {
            this.isShow = data.show;
        })

        // 点击网页全屏触发的事件
        this.mulplayer.on('pagefull-change', data => {
            if (data.pagefull) {
                // 如果当前是全屏设置弹幕宽度
                if (this.$refs['barrage'] != null) {
                    this.$refs['barrage'].resize();
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
                if (this.$refs['barrage'] != null) {
                    this.$refs['barrage'].resize();
                }
                this.isFullScreen = true;
            } else {
                this.isFullScreen = false;
            }
        });

        // 当用户拖动进度条时触发
            let lastTime = this.video.currentTime;
            this.mulplayer.on("seek-progress", (data) => {
                // 清空弹幕
                this.tempBarrageList = [];
                    let barrages = this.barrageListTotal;
                    let len = barrages.length;
                    for (let i = 0; i < len; i++) {
                        if (barrages[i].courseVideoTime >= data.percentage * this.video.duration / 100) {
                            this.tempBarrageList.push(barrages[i]);
                        }
                    }
            })

            // 当视频暂停时触发
            this.video.addEventListener("pause", () => {
                // 暂停弹幕
                if (this.$refs["barrage"] != null) {
                    this.$refs["barrage"].pause();
                }
                
            })

            // 当视频播放时触发
            this.video.addEventListener("play", () => {
                // 开始弹幕播放
                this.$refs["barrage"].play();
            })
        },
        mousemove() {
        },
    },
    unmounted() {
        WebSocketServer.onClose();
        this.mulplayer.on('destroy', () => {
            alert("already-destroy")
        })
    },
    pause() {
    },
    end() {
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
.convert {
    font-size: 20px; 
    font-weight: 600;
    cursor: pointer;
}
.outer-barrage-content {
    width: 40%;
}
.outer-barrage-button {
    font-size: 30px;
}
.video-bottom {
    display: flex; 
    align-items: center;
    padding: 20px 0px 0px 0px; 
    
}
.space {
    margin: 0px 10px;
}
.outer-barrage-speed-number {
    position: absolute;
    top: 21vh;
    right: 2vw;
    font-size: 14px;
}
.barrage-speed-number {
    position: absolute;
    top: 18.5vh;
    right: 3vw;
    font-size: 14px;
}
.outer-barrage-font-size-number {
    position: absolute;
    top: 16.5vh;
    right: 2vw;
    font-size: 14px;
}
.barrage-font-size-number {
    position: absolute;
    top: 14.5vh;
    right: 3vw;
    font-size: 14px;
}
.outer-show-area-number {
    position: absolute;
    top: 11.5vh;
    right: 2vw;
    font-size: 14px;
}
.show-area-number {
    position: absolute;
    top: 10vh;
    right: 3vw;
    font-size: 14px;
}
.outer-no-transparent-number {
    position: absolute;
    top: 6.5vh;
    right: 2vw;
    font-size: 14px;
}
.outer-no-transparent-number::after {
    content: "%";
}
.no-transparent-number {
    position: absolute;
    top: 5.5vh;
    right: 3vw;
    font-size: 14px;
}
.no-transparent-number::after {
    content: "%";
}
.outer-show-area {
    position: absolute;
    top: 11.5vh;
    left: 3vw;
    font-size: 14px;
}
.show-area {
    position: absolute;
    top: 10vh;
    left: 3vw;
    font-size: 14px;
}
.outer-barrage-font-size {
    position: absolute;
    top: 16.2vh;
    left: 3vw;
    font-size: 14px;
}
.barrage-font-size {
    position: absolute;
    top: 14.2vh;
    left: 3vw;
    font-size: 14px;
}
.outer-barrage-speed {
    position: absolute;
    top: 21vh;
    left: 3vw;
    font-size: 14px;
}
.barrage-speed {
    position: absolute;
    top: 18.5vh;
    left: 3vw;
    font-size: 14px;
}

.outer-no-transparent {
    position:absolute;
    top: 6.5vh;
    left: 3vw;
    font-size: 14px;
}

.no-transparent {
    position:absolute;
    top: 5.5vh;
    left: 3vw;
    font-size: 14px;
}
.block {
    width: 80%;
    
}
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
    left: 600px;
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
.outer-barrage-setting-tips {
    position: absolute;
    background-color: rgba(0, 0, 0, 0.5);
    color: white;
    top: -27vh;
    left: 16vw;
    width: 24vw;
    padding: 20px;
    padding-left: 8vw;
    animation: take-in 0.4s linear;
}
.barrage-setting-tips {
    position: absolute;
    background-color: rgba(0, 0, 0, 0.5);
    opacity: 0.9;
    color: white;
    top: -27vh;
    left: -8vw;
    width: 24vw;
    padding: 20px;
    padding-left: 8vw;
    animation: take-in 0.4s linear;
}
.barrage-tips {
    position: absolute;
    background-color: black;
    opacity: 0.9;
    color: white;
    top: -5vh;
    left: 3.5vw;
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
.danmakuClass {
    width: 100%;
    height: 100%;
    font-weight: bold;
}

.danmaku-class {
    position: absolute;
    color: white;
    font-size: 30px;
    cursor: pointer;
    left: 6vw;
    animation: barrage-content-parent 0.22s linear;
}
.danmaku-setting-class {
    color: white;
    position: absolute;
    top: 0;
    left: 9vw;
    z-index: 20000;
    font-size: 30px;
    cursor: pointer;
    animation: barrage-content-parent 0.22s linear;
}

.outer-danmaku-setting-class {
    font-size: 30px;
    cursor: pointer;
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


</style>