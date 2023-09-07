<template>
    <div class="MonkeyWebCommunityMember-container">
        <el-row class="position">
            <el-col :span="11" >
                <div v-for="role in roleList" :key="role.id" @click="showData(role)">
                    <div :class="['title', 
                    {'el-icon-caret-right': !role.selected},
                    {'el-icon-caret-bottom': role.selected}]">
                        <span class="title-left">
                            <span class="title-content">
                                &nbsp;{{ role.roleName }}
                            </span> 
                            <span class="role-people">{{ role.count }}</span>
                        </span>
                        <el-checkbox 
                        v-show="role.selected"
                        @change="allSelected(role)"
                        v-model="role.allSelected" 
                        ></el-checkbox>
                    </div> 
                    <div class="userInfo" v-if="role.selected"  v-for="user in role.userList" :key="user.id" @click.stop="selectedStudent(role, user)">
                        <img class="head-img" :src="user.photo" alt="">
                        <span class="username">{{ user.username }}</span>
                        <el-checkbox v-model="user.selected" @change="selectedStudent(role, user)"></el-checkbox>
                    </div>
                </div>
            </el-col>
            <el-col :span="2"><div class="divider">&nbsp;</div></el-col>
            <el-col :span="11">
                <div class="sum">已选择 {{ studentCount }} / {{ studentSum }} 成员</div>
                <div class="right-info" v-for="selectedUser in selectedUserList" :key="selectedUser.id">
                    <img class="head-img-selected" :src="selectedUser.photo" alt="">
                    <span class="username">{{ selectedUser.username }}</span>
                    <span @click="cancelSelected(selectedUser)" class="el-icon-error" style="opacity: 0.5;"></span>
                </div>
            </el-col>
        </el-row>
        
        <el-row class="button">
            <el-button 
            size="mini" 
            style="width: 20%;" 
            @click="toTaskConfiguration()">返回</el-button>
            <el-button 
            size="mini" 
            style="width: 20%;" 
            type="primary"
            @click="confirmTaskPeople()">确定</el-button>
        </el-row>
    </div>
</template>

<script>
import $ from "jquery"
import store from "@/store";
export default {
    name: 'MonkeyWebCommunityMember',

    data() {
        return {
            // 社区 id
            communityId: "",
            checked: true,
            publishCommunityUrl: "http://localhost:80/monkey-community/publish",
            // 社区角色列表
            roleList: [],
            // 被选中的成员
            selectedUserList: [],
            // 被选中的学生数
            studentCount: 0,
            // 学生总数
            studentSum: 0,
        };
    },
    created() { 
        this.communityId = this.$route.params.communityId;
        this.queryCommunityRoleList(this.communityId);
    },
    methods: {
        // 确定任务成员
        confirmTaskPeople() {
            this.$emit("confirmTaskPeople", this.selectedUserList);
        },
        // 全部选择或全部取消按钮
        allSelected(role) {
            if (role.allSelected) {
                // 全部选中
                const userList = role.userList;
                for (let i = 0; i < userList.length; i ++ ) {
                    let idx = this.selectedUserList.indexOf(userList[i]);
                    if (idx == '-1') {
                        this.studentCount ++ ;
                        role.selectedSum ++ ;
                        userList[i].selected = true;
                        this.selectedUserList.push(userList[i]);
                    }
                }
            } else {
                // 全部取消
                const userList = role.userList;
                for (let i = 0; i < userList.length; i++) {
                    let idx = this.selectedUserList.indexOf(userList[i]);
                    if (idx != '-1') {
                        this.studentCount--;
                        role.selectedSum -- ;
                        userList[i].selected = false;
                        this.selectedUserList.splice(idx, 1);
                    }
                }
            }
        },
        // 取消选中的成员
        cancelSelected(user) {
            user.selected = false;
            this.selectedUserList.splice(this.selectedUserList.indexOf(user), 1);
            this.studentCount--;

            let roleId = user.communityRoleId;
            for (let i = 0; i < this.roleList.length; i++) {
                if (roleId == this.roleList[i].id) {
                    this.roleList[i].allSelected = false;
                    this.roleList[i].selectedSum--;
                    break;
                }
            }
        },
        // 选中成员加入已选择
        selectedStudent(role, user) {
            if (!user.selected) {
                // 说明选中成员
                user.selected = true;
                this.studentCount++;
                role.selectedSum++;
                // 可以快速判断该用户属于哪个角色
                // 以便更快的找到删除元素
                user.communityRoleId = role.id;
                this.selectedUserList.push(user);
                
            } else {
                // 取消选则该成员
                user.selected = false;
                this.studentCount--;
                role.selectedSum--;
                this.selectedUserList.splice(this.selectedUserList.indexOf(user), 1);
            }

            // 判断是否全选
            if (role.selectedSum == role.userList.length) {
                role.allSelected = true;
            } else {
                role.allSelected = false;
            }
        },
        // 是否展示数据
        showData(role) {
            if (!role.selected) {
                role.selected = true;
            } else {
                role.selected = false
            }
        },
        // 查询社区角色列表
        queryCommunityRoleList(communityId) {
            const vue = this;
            $.ajax({
                url: vue.publishCommunityUrl + "/queryCommunityRoleList",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    communityId,
                },
                success(response) {
                    if (response.code == '200') {
                        vue.roleList = response.data.communityRoleList;
                        vue.studentSum = response.data.sum;
                    } else {
                        vue.$modal.msgError(response.msg);
                    }
                }
            })
        },
        // 返回任务选择界面
        toTaskConfiguration() {
            this.$emit("returnTask");
        }
    },
};
</script>

<style scoped>
.role-people {
    vertical-align: middle;
}
.title-left {
    display: inline-block;
    max-width: 85%;
    min-width: 85%;
}
.button {
    text-align: center;
    position: absolute;
    background-color: #FFFFFF;
    top: 85%;
    width: 600px;
    left: 50%;
    transform: translate(-50%, -50%);
    padding: 20px;
    overflow: auto;
    animation: slide-up 0.4s linear;
}
.el-icon-error {
    cursor: pointer;
}
.right-info {
    margin-bottom: 10px;
    padding: 5px 10px;
}
.sum {
    font-size: 14px;
    padding: 10px;
}
.divider {
    height: 460px;
    width: 1px;
    background-color: gray;
    margin: 0 auto;
    opacity: 0.5;
}
.title-content {
    margin-right: 10px;
    max-width: 90%;
    display: inline-block;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.userInfo {
    margin: 5px 0 5px 0;
    padding: 5px 0 5px 0;
    width: 100%;
}
.userInfo:hover {
    background-color: #F4F4F4;
    cursor: pointer;
}
.username {
    display: inline-block;
    max-width: 69.6%;
    min-width: 69.6%;
    line-height: 24px;
    height: 24px;
    font-size: 14px;
    vertical-align:middle;
    margin-left: 10px;
    overflow: hidden;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}
.head-img-selected {
    width: 24px;
    height: 24px;
    line-height: 24px;
    vertical-align:middle;
}
.head-img {
    width: 24px;
    height: 24px;
    line-height: 24px;
    vertical-align:middle;
    margin-left: 20px;

}
.title {
    font-size: 14px;
    font-weight: 600;
    width: 100%;
    padding: 10px 0;
}
.title:hover {
    cursor: pointer;
    background-color: #F4F4F4;
}
.MonkeyWebCommunityMember-container {
    z-index: 20000;
    position: fixed;
    height: 100%; 
    width: 100%; 
    background-color: rgba(0, 0, 0, 0.5);
    top: 0;
    left: 0;
}

.position {
    border-radius: 10px;
    position: absolute;
    width: 600px;
    background-color: #FFFFFF;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    padding: 20px;
    min-height: 500px;
    max-height: 500px;
    overflow: auto;
    animation: slide-up 0.4s linear;
}
@keyframes slide-up {
    0% {
        opacity: 0;
    }
    100% {
        opacity: 1;
    }
}

</style>