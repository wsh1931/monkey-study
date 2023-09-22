<template>
    <div class="MonkeyWebVoteConfiguration-container">
        <div class="position">
            <div class="el-icon-close close" @click="cancel()"></div>
            <h2 style="text-align: center;">添加投票</h2>
            <el-alert
                title="审核通过后，将在文章底部展示。若该投票有人参与，无法进行编辑操作"
                type="warning"
                :closable="false"
                show-icon>
            </el-alert>
            
            <el-form 
            :model="vote" 
            :rules="rules" 
            ref="vote" 
            label-width="auto" 
            class="demo-vote" 
            label-position="right">
                <el-form-item label="投票名称" prop="voteName">
                    <el-input v-model.number="vote.voteName" placeholder="请输入投票名称"></el-input>
                </el-form-item>
                
                <el-form-item label="投票种类" prop="voteKind">
                    <el-radio-group v-model="vote.voteKind">
                    <el-radio label="0">单选</el-radio>
                    <el-radio label="1">多选</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item prop="voteDuration" label="截止时间" required>
                    <el-date-picker
                    style="z-index: 1;"
                    v-model="vote.voteDuration"
                    align="right"
                    type="date"
                    placeholder="选择日期"
                    :picker-options="pickerOptions">
                    </el-date-picker>
                </el-form-item>

                <el-form-item prop="selectCount" label="选项内容" required style="height: 20px;">
                    <span class="select-count">最多可添加 {{ vote.selectCount }} / 10 个选项</span>
                </el-form-item>

                <el-form-item
                v-for="(voteItem, index) in vote.communityArticleVoteItemList"
                :key="voteItem.key"
                :prop="'communityArticleVoteItemList.' + index + '.voteContent'"
                label-width="78px"
                :rules="{
                    required: true, message: '选项内容在 1 ~ 30 个字符之间', trigger: 'blur'
                }"
                >
                    <el-input v-model="voteItem.voteContent" style="width: 90%;" placeholder="请输入选项内容"></el-input>
                    <span class="el-icon-delete delete-button" @click.prevent="removeDomain(voteItem)"></span>
                    
                </el-form-item>
                <el-button 
                type="text" 
                class="el-icon-circle-plus-outline add-item" 
                @click="addDomain()" 
                size="mini">&nbsp;添加选项</el-button>
                <el-form-item style="text-align: right;">
                    <el-button @click="cancel()" size="medium">取消</el-button>
                    <el-button type="primary" @click="submitForm('vote')" size="medium">确认配置</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebVoteConfiguration',

    data() {
        var checkSelectCount = (rule, value, callback) => {
            if (value < 2) {
                return callback(new Error('至少为两个选项'));
            }
            setTimeout(() => {
                if (!Number.isInteger(value)) {
                    callback(new Error('请添加选项内容'));
                } else {
                    if (value < 2 || value > 10) {
                    callback(new Error('选项数在 2 ~ 10个之间'));
                    } else {
                    callback();
                    }
                }
            }, 1000);
        };
        return {
            vote: {
                selectCount: 2,
                voteName: "",
                voteDuration: "",
                communityArticleVoteItemList: [
                    {
                        voteContent: "",
                        key: Date.now()
                    },
                    {
                        voteContent: "",
                        key: Date.now()
                    },
                ],
            },
            rules: {
                endTime: [
                    { type: 'date', required: true, message: '请选择日期', trigger: 'change' }
                ],
                voteDuration: [
                    { type: 'date', required: true, message: '请选择投票结束日期', trigger: 'change' }
                ],
                voteName: [
                    { required: true, message: '请输入投票名称', trigger: 'blur' },
                    { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
                ],
                voteKind: [
                    { required: true, message: '请选择投票种类', trigger: 'change' }
                ],
                selectCount: [
                    { validator: checkSelectCount, trigger: 'change' }
                ]
            },
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() <= Date.now();
                },
                shortcuts: [
                {
                    text: '今天',
                    onClick(picker)
                    {
                        picker.$emit('pick', new Date());
                    }
                },
                {
                    text: '明天',
                    onClick(picker)
                    {
                        const date = new Date();
                        date.setTime(date.getTime() + 3600 * 1000 * 24);
                        picker.$emit('pick', date);
                    }
                },
                {
                    text: '一周后',
                    onClick(picker)
                    {
                        const date = new Date();
                        date.setTime(date.getTime() + 3600 * 1000 * 24 * 7);
                        picker.$emit('pick', date);
                    }
                    },
                {
                    text: '一月后',
                    onClick(picker)
                    {
                        const date = new Date();
                        date.setTime(date.getTime() + 3600 * 1000 * 24 * 30);
                        picker.$emit('pick', date);
                    }
                }
                ]
            },
        };
    },

    methods: {
        removeDomain(item) {
            if (this.vote.selectCount <= 2) {
                this.$modal.msgWarning("最少要存在 2 个选项");
                return;
            }
            var index = this.vote.communityArticleVoteItemList.indexOf(item)
            if (index !== -1) {
                this.vote.selectCount -- ;
                this.vote.communityArticleVoteItemList.splice(index, 1)
            }
        },
        addDomain() {
            if (this.vote.selectCount >= 10) {
                this.$modal.msgWarning("最多存在 10 个选项");
                return;
            }
            this.vote.selectCount ++ ;
            this.vote.communityArticleVoteItemList.push({
            voteContent: '',
            key: Date.now()
            });
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
            if (valid) {
                this.$emit('updateVote', this.vote);
            } else {
                alert("error")
            }
        });
        },
        // 取消以任务形式发布
        cancel() {
            this.$emit("cancelVote");
        }
    },
};
</script>

<style scoped>
.add-item {
    display: block;
    margin-left: 78px;
    margin-top: -10px;
}
.select-count {
    font-size: 12px;
    color: gray;
}
.demo-vote {
    margin-top: 10px;
}
.delete-button {
    margin-left: 20px;
    font-size: 16px;
    cursor: pointer;
}
.delete-button:hover {
    opacity: 0.5;
}
.close {
    position: absolute;
    right: 10px;
    top: 20px;
    font-size: 20px;
    cursor: pointer;
}
.MonkeyWebVoteConfiguration-container {
    z-index: 20000;
    position: fixed;
    height: 100%; 
    width: 100%; 
    background-color: rgba(0, 0, 0, 0.5);
    top: 0;
    left: 0;
}
.position {
    position: absolute;
    width: 500px;
    background-color: #FFFFFF;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    padding: 20px;
    max-height: 600px;
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