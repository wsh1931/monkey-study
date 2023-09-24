<template>
    <!-- 分页功能 -->
    <div class="block">
        <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="parseInt(totals)">
        </el-pagination>
    </div>
</template>

<script>
export default {
    name: "PagiNation",
    props: {
        currentPage: Number,
        pageSize: Number,
        totals: Number,
    },

    
    methods: {
        handleSizeChange(val) {
            this.$emit("handleSizeChange", val)
        },
        handleCurrentChange(val) {
            this.$emit('handleCurrentChange', val);
        },
    }
}
</script>

<style scoped>

</style>