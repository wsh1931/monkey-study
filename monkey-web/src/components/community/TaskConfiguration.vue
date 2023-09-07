<template>
    <div class="MonkeyWebTaskConfiguration-container">
        <div class="position">
            <div class="el-icon-close close" @click="cancel(false)"></div>
            <h2 style="text-align: center;">任务配置</h2>
            <el-form :model="task" :rules="rules" ref="task" label-width="100px" class="demo-task" label-position="top" >
                <el-form-item label="完成任务后获得的默认积分" prop="finishScore">
                    <el-input v-model.number="task.finishScore" placeholder="输入分数的范围为 0 ~ 9999"></el-input>
                </el-form-item>
                
                <el-form-item label="请选择提交结果是否公开" prop="isPublic">
                    <el-radio-group v-model="task.isPublic">
                    <el-radio label="0">不公开</el-radio>
                    <el-radio label="1">公开</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="请选择任务接取方式" prop="receiverWay">
                    <el-radio-group v-model="task.receiverWay">
                    <el-radio label="0">不指定用户</el-radio>
                    <el-radio label="1">指定用户</el-radio>
                    </el-radio-group>
                    <el-button 
                    style="margin-left: 10px;"
                    size="mini"
                    v-if="task.receiverWay == '1'"
                    @click="addMember()"
                    class="el-icon-circle-plus-outline">&nbsp;添加成员</el-button>
                </el-form-item>
                <el-form-item prop="endTime" label="请选择任务截止时间" required>
                    <el-date-picker
                    style="z-index: 1;"
                    v-model="task.endTime"
                    align="right"
                    type="date"
                    placeholder="选择日期"
                    :picker-options="pickerOptions">
                    </el-date-picker>
                </el-form-item>
                <el-form-item style="text-align: right;">
                    <el-button @click="cancel('task')" size="mini">取消</el-button>
                    <el-button type="primary" @click="submitForm('task')" size="mini">确认配置</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebTaskConfiguration',

    data() {
        var checkFinishScore = (rule, value, callback) => {
            if (!value && value != '0') {
                return callback(new Error('分数不能为空'));
            }
            setTimeout(() => {
                if (!Number.isInteger(value)) {
                    callback(new Error('请输入数字值'));
                } else {
                    if (value < 0 || value > 9999) {
                    callback(new Error('分数必须在 0 ~ 9999之间'));
                    } else {
                    callback();
                    }
                }
            }, 1000);
        };
        return {
            task: {
                finishScore: '',
                isPublic: '',
                receiverWay: '',
            },
            rules: {
                finishScore: [
                    { validator: checkFinishScore, trigger: 'change' }
                ],
                receiverWay: [
                    { required: true, message: '请选择任务接取方式', trigger: 'change' }
                ],
                endTime: [
                    { type: 'date', required: true, message: '请选择任务结束日期', trigger: 'change' }
                ],
                isPublic: [
                    { required: true, message: '请选择提交结果是否公开', trigger: 'change' }
                ],
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

    mounted() {
        
    },

    methods: {
        // 打开添加成员界面
        addMember() {
            this.$emit("openAddMember");
        },
        submitForm(formName) {
            this.$refs[formName].validate((valid) => {
            if (valid) {
                this.$emit('updateTask', this.task);
            }
        });
        },
        // 取消以任务形式发布
        cancel() {
            this.$emit("cancelTask", false);
        }
    },
};
</script>

<style scoped>
.close {
    position: absolute;
    right: 10px;
    top: 20px;
    font-size: 20px;
    cursor: pointer;
}
.close:hover {
    opacity: 0.5;
}
.MonkeyWebTaskConfiguration-container {
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
    width: 480px;
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