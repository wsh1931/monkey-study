<template>
    <div class="InterlocationCard-container">
        <el-card class="hover-border box-card">
            <el-row>
                <el-col :span="4">
                    <el-row>
                        <el-tag type="success">回答 20</el-tag>
                    </el-row>
                    <el-row style="margin-top: 30px;">
                        <el-tag type="warning">阅读 19</el-tag>
                    </el-row>
                </el-col>
                <el-col :span="20">
                    <el-row>
                        <el-col :span="16">问答标题</el-col>
                        <el-col :span="4" style="margin-top: 10px;">2023-06-16</el-col>
                        <el-col :span="4">
                            <el-button icon="el-icon-question" type="primary" size="medium" round>回答</el-button>
                        </el-col>
                    </el-row>
                    <el-row style="width: 70%; margin-top: 20px; text-align: left;">
                        <el-col :span="9">
                            <el-row class="hover">
                                <el-col :span="9">
                                    <img class="hover" style="width: 40px; height: 40px; border-radius: 50%;" src="https://monkey-blog.oss-cn-beijing.aliyuncs.com/userPhoto/2023/06/15/cc698426ba0440fc827df7720d41b06104.png" alt="">
                                </el-col>
                                <el-col :span="15">
                                    <div class="information hover" style="margin-top: 10px;">吴思豪</div>
                                </el-col>
                            </el-row>
                            
                        </el-col>
                        <el-col :span="5" class="el-icon-view information hover">
                            游览 18
                        </el-col>
                        <el-col :span="5" class="information el-icon-chat-dot-square hover">
                            回答 22
                        </el-col>
                        <el-col :span="5" class="information el-icon-user-solid hover">
                            关注 12
                        </el-col>
                    </el-row>

                </el-col>
            </el-row>
        </el-card> 
    </div>
</template>

<script>
 export default {
    name: "InterlocationCard",
 }
</script>

<style scoped>
.ellipsis {
    color: gray;
  display: block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-align: left;
}

.box-card {
    border-radius: 20px;
  
    border-radius: 2px;
    margin-bottom: 20px;
    border: 1px solid #dcdfe6;
    -webkit-box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    }
.hover-border:hover {
    box-shadow: 0 0 5px 5px lightblue;
}
.hover:hover {
    cursor: pointer;
    color: #46A1FF;
}

.information {
    margin-top: 12px; 
    color: rgba(0, 0, 0, 0.5);
     font-size: 14px;
}
</style>