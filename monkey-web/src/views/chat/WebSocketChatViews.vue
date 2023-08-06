<template>
    <div class="WebSocketChat-container">
        <el-container class="el-container">
        <el-aside width="300px" style="background-color: #E8E8E8;">  
            <el-row>
                <el-col :span="6">
                    <img :src="$store.state.user.photo"
                        class="img"
                        alt="">
                </el-col>
                <el-col :span="18" style="text-align: left; margin-top: 20px;" class="more-hide" >
                    {{ $store.state.user.username }}
                </el-col>
            </el-row>
            <el-row>
                <el-input @input="getUserListByUsername(username)" v-model="username"  placeholder="请输入用户名称" class="input-style">
                    <template slot="prepend">
                        <i class="el-icon-search"></i>
                    </template>
                </el-input>
            </el-row>
            <el-row >
                <el-row 
                :class="['hover', {selected:isSelected == charUserInformation.id}]" v-for="charUserInformation in chatUserInformationList" :key="charUserInformation.id" >
                    <div
                    v-if="charUserInformation.receiverId != $store.state.user.id" 
                    @click="showChatInformation(charUserInformation.receiverId, charUserInformation.senderId, charUserInformation.id)" >
                        <div @click="showRow(charUserInformation.id)"> 
                        <el-col :span="6">
                            <img :src="charUserInformation.receiverPhoto"
                             width="50px"
                              height="50px"
                               style="border-radius: 50%;margin-left: 15px; margin-top: 5px;" alt="">
                        </el-col>
                        <el-col :span="18" style="text-align: left;">
                            <el-row >
                                <el-col class="more-hide" :span="12">
                                    {{ charUserInformation.receiverName }}
                                </el-col>
                                <el-col :span="4" v-if="charUserInformation.isLike == '0'">
                                    <div class="concern">
                                        <span>未关注</span>
                                    </div>
                                </el-col>
                                <el-col :span="4" v-else>
                                    <div class="concern">
                                        <span>
                                            已关注
                                        </span>
                                    </div>
                                </el-col>
                                <el-col :span="8" style="font-size: 10px; color: rgba(0, 0, 0, 0.5);">
                                    {{ charUserInformation.lastCreateTime | formatDate }}
                                </el-col>
                            </el-row>
                            <el-row class="more-hide">
                                {{ charUserInformation.lastContent }}
                            </el-row>
                        </el-col>
                    </div>
                </div>
                <div
                    v-else
                    @click="showChatInformation(charUserInformation.senderId, charUserInformation.receiverId, charUserInformation.id)" >
                        <div @click="showRow(charUserInformation.id)"> 
                        <el-col :span="6">
                            <img :src="charUserInformation.senderPhoto"
                            class="img-3"
                              alt="">
                        </el-col>
                        <el-col :span="18" style="text-align: left;">
                            <el-row >
                                <el-col class="more-hide" :span="12">
                                    {{ charUserInformation.senderName }}
                                </el-col>
                                <el-col :span="4" v-if="charUserInformation.isLike == '0'">
                                    <div class="concern">
                                        <span>未关注</span>
                                    </div>
                                </el-col>
                                <el-col :span="4" v-else>
                                    <div class="concern">
                                        <span>
                                            已关注
                                        </span>
                                    </div>
                                </el-col>
                                <el-col :span="8" style="font-size: 10px; color: rgba(0, 0, 0, 0.5);">
                                    {{ charUserInformation.lastCreateTime | formatDate }}
                                </el-col>
                            </el-row>
                            <el-row class="more-hide last-content" >
                                {{ charUserInformation.lastContent }}
                            </el-row>
                        </el-col>
                    </div>
                </div>
                </el-row>
            </el-row>
        </el-aside>
        <el-main class="is-choice" v-if="isChoice">
            <el-row>
              <span class="more-hide" style="width: 400px;">{{ $store.state.user.username }}</span>
            </el-row>
            <br>
            <el-row class="chatBox">
                <el-row  v-for="message in messageList" :key="message.id">
                    <el-row  style="margin-top: 10px;">
                    <div v-if="message.direction == '右'" class="right"> 
                        <el-col :span="22" >
                            <el-row>
                                <span class="message-createTime2">
                                    {{ message.createTime }}
                                </span>
                                <span>
                                    <!-- 啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊 -->
                                    {{ message.senderName }}
                                </span>
                                
                            </el-row>
                            <el-row class="message-content2">
                                {{ message.content }}
                            </el-row>
                        </el-col>
                        <el-col :span="2">
                            <img 
                            @click="toUserHome(message.senderId)" 
                            class="message-img"
                            :src="message.senderPhoto"/>
                        </el-col>
                    </div>
                    <div v-else-if="message.direction == '左'" style="width: 70%">
                        <el-col :span="2">
                           
                            <img 
                             @click="toUserHome(message.senderId)"
                             class="sender-message"
                              :src="message.senderPhoto"/>
                        </el-col>
                        <el-col :span="22" style="text-align: left;">
                            <el-row >
                                <span>
                                    <!-- 啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊 -->
                                    {{ message.senderName }}
                                </span>
                                <span class="message-createTime">
                                    {{ message.createTime }}
                                </span>
                            </el-row>
                            <el-row class="message-content">
                                {{ message.content }}
                            </el-row>
                        </el-col>
                    </div>
                </el-row>
                </el-row>
            </el-row>
                <el-input
                    v-model="message"
                    type="textarea"
                    :autosize="{ minRows: 5 , maxRows: 5}"
                    placeholder="按Enter发送，Ctrl + Enter换行, 只会保留最近前10条消息。"
                    :show-word-limit="true"
                    text-autosize
                    resize="none"
                    minlength="1"
                    maxlength="1000"
                    @keydown.native="handleKeyDown($event)"
                >
                </el-input>
        </el-main>


        <el-main v-else style="background-color: white;">
            <el-row>
                请选择需要聊天的用户
            </el-row>
            <el-divider></el-divider>
            <el-row style="height: 350px">
            </el-row>
            <el-divider></el-divider>
        </el-main>
        </el-container>
    </div>
</template>

<script>
import $ from "jquery"
import store from "@/store";
import WebSocketServer from "@/socket/WebSocketServer";

export default {
    name: "WebSocketChat",
    data() {
        return {
            userCenterHomeUrl: "http://localhost:80/monkey-user/monkey-user/user/center/home",
            webSocketChatUrl: "http://localhost:80/monkey-netty/webSocketChat",
            // 是否点击了左边框
            isChoice: false,
            //聊天用户信息
            chatUserInformationList: [],
            // 右边框展示信息
            showInformation: {
                receiverName: "",
                receiverBrief: "",
            },
            socketUrl: `ws://localhost:80/websocket/chat/${store.state.user.token}`,
            // 聊天消息
            messageList: [],
            // 是否选中该行
            isSelected: null,
            // 聊天框发送消息
            message: "",
            // 接收人id
            receiverId: "",
            //消息种类，sendMessage表示发送消息. receiveMessage 表示接收消息
            messageKind: "", 
            // 初始化时的接收人id
            startReceiverId: "",
            // 通过用户名模糊查找用户信息
            username: "",
        }
    },

    beforeDestroy() {
        WebSocketServer.onClose();
    },

    // 每次点击人之后自动跳到页面最底部
    updated(){
        let scrollContainer = document.querySelector('.chatBox')
        scrollContainer.scrollTop = scrollContainer.scrollHeight
    },

    filters: {
        formatDate: value => {
        if (!value) return '';

        // 转换成 Date 对象
        const date = new Date(value);

        // 格式化输出
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);

        return `${year}-${month}-${day}`;
        }
    },

    created() {
        if (store.state.user.token == null || store.state.user.token == "") {
            this.$modal.msgError("请先登录");
            return ;
        }
        this.startReceiverId = this.$route.params.receiverId;
        this.initWebSocket();
        this.getReplyUserListByUserId(this.startReceiverId);
    },
    methods: {
        // 点击跳到个人主页
        toUserHome(userId) {
            // 跳转之前该用户最近游览信息加入作者主页
            const vue = this;
            $.ajax({
                url: vue.userCenterHomeUrl + "/recentlyView",
                type: "post",
                data: {
                    userId,
                    reviewId: store.state.user.id
                },
                success(response) {
                    if (response.code == '200') {
                        vue.$router.push({
                        name: "user_home",
                        params: {
                            userId
                        }
                    })
                    } else {
                        vue.$modal.msgError("发送未知错误，查看作者主页失败")
                    }
                },
                error() {
                    vue.$modal.msgError("发送未知错误，查看作者主页失败")
                }
            })
        },
        // 通过用户名模糊查找用户信息中的用户名
        async getUserListByUsername(username) {
            const vue = this;
            await vue.getReplyUserListByUserId(vue.startReceiverId)
            $.ajax({
                url: vue.webSocketChatUrl + "/getUserListByUsername",
                type: "get",
                dataType:"json",
                contentType:"application/json",
                data: {
                    username,
                    userChatVo: JSON.stringify(vue.chatUserInformationList)
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == '200') {
                        vue.chatUserInformationList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
            })
        },
        handleKeyDown(e) {
        if (e.keyCode === 13 && !e.ctrlKey) {
            // Enter，发送消息
            this.sendMessages(this.message, this.receiverId);
            e.preventDefault();
        } else if (e.keyCode === 13 && e.ctrlKey) {
            // Ctrl+Enter，换行
            this.message += '\n';
        }
        },
        // 发送聊天消息
        sendMessages(message, receiverId) {
        // 发送消息的逻辑
        WebSocketServer.send({
                event: "send_message",
                message,
                receiverId
            });

            this.message = "";
        },
        initWebSocket() {
            // 创建WebSocket

            WebSocketServer.connect(this.socketUrl);
            WebSocketServer.onMessage((message) => {
                // 接收回调函数后的第三步
                if (message.event == "start_chat") {
                    this.messageList = message.messageList;
                } else if (message.event == "send_message") {
                    if (message.information != null) this.messageList.push(message.information)
                } else if (message.event == "receive_message") {
                    if (message.information != null) this.messageList.push(message.information)
                }
        })
        },
        // 展示所选行
        showRow(index) {
            this.isSelected = index;
        },
        // 得到聊天对话框信息
        showChatInformation(receiverId, senderId, index) {
            this.isChoice = true;
            const vue = this;
            this.isSelected = index
            this.receiverId = receiverId;
            $.ajax({
                url: vue.webSocketChatUrl + "/showChatInformation",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                data: {
                    senderId,
                    receiverId,
                },
                success(response) {
                    if (response.code == '200') {
                        WebSocketServer.send({
                            event: "start_chat",
                            message: response.data
                        });
                    } else {
                        vue.$modal.msgError("发生未知错误，加载信息失败");
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
            })
        },
        // 通过当前登录用户登录id得到该用户聊天列表
        async getReplyUserListByUserId(receiverId) {
            const vue = this;
           await $.ajax({
                url: vue.webSocketChatUrl + "/getReplyUserListByUserId",
                type: "get",
                data: {
                    receiverId
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token
                },
                success(response) {
                    if (response.code == "200") {
                        vue.chatUserInformationList = response.data;
                    } else {
                        vue.$modal.msgError("发生未知错误")
                    }
                },
                error() {
                    vue.$modal.msgError("认证失败，无法访问系统资源");
                }
            })
        }
    }
}
</script>

<style scoped>
.WebSocketChat-container {
    text-align: center;
    overflow:auto;
}
.is-choice {
    background-color: white; 
    padding: 0px;
}
.img-3 {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-left: 15px;
    margin-top: 5px;
}
.chatBox {
    height: 490px; 
    overflow: auto;
    padding: 10px;
}
.message-createTime2 {
    color: rgba(0, 0, 0, 0.5); 
    font-size: 10px; 
    margin-right: 10px;
}
.right {
    text-align: right; 
    width: 70%; 
    margin-left: 195px;
}c
.last-content {
    font-size: 14px; 
    color: rgba(0, 0, 0, 0.5); 
    margin-top: 5px;
}
.concern {
    background-color: #F7F7FC; 
    font-size: 10px; 
    color: rgba(0, 0, 0, 0.5);
}
.message-content2 {
    background-color: #E7F7FF; 
    display: flex; 
    flex-wrap: wrap; 
    padding: 5px;
    font-size: 16px;
}
.sender-message {
    width: 40px;
    height: 40px;
    border-radius: 50%; 
    cursor: pointer;
    margin-top: 5px;
}
.message-img {
    width: 40px;
    height: 40px; 
    border-radius: 50%; 
    margin-top: 5px;
    cursor: pointer; 
}
.message-createTime {
    color: rgba(0, 0, 0, 0.5); 
    font-size: 10px; 
    margin-left: 10px;
}
.message-content {
    background-color: #F4F6F8; 
    display: flex; 
    flex-wrap: wrap; 
    padding: 5px; 
    font-size: 16px;
}
.img {
     width: 50px;
    height: 50px;
    border-radius: 50%;
    margin-top: 10px;
    margin-left: 10px;
}
.el-container {
    width: 70%;
    margin-left: 200px; 
    padding: 0px;
    height: 653px; 
    margin-top: 15px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
}
span {
  display: inline-block; /* 将span元素设置为块级元素，方便设置宽度 */
  max-width: 200px; /* 最大宽度为200px，当宽度超出时会出现省略号 */
  white-space: nowrap; /* 触发文本溢出和隐藏 */
  overflow: hidden; /* 触发文本溢出和隐藏 */
  text-overflow: ellipsis; /* 显示省略号 */
}
.selected {
    background-color: #F5F7FA;
}
.hover:hover {
    background-color: #F5F7FA;
    cursor: pointer;
}
.more-hide {
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
}
  .text {
    font-size: 14px;
  }

  .item {
    margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
  }
  .clearfix:after {
    clear: both
  }

  .box-card {
    width: 480px;
  }
</style>