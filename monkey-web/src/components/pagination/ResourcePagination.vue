<template>
    <div>
        <!-- 分页功能 -->
        <div class="block">
            <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[70]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="parseInt(totals)">
            </el-pagination>
        </div>
    </div>
</template>

<script>
export default {
    name: 'MonkeyWebResourcePagination',
    props: ['currentPage', 'pageSize', 'totals'],
    data() {
        return {
        handleSizeChange(val) {
            this.$emit("handleSizeChange", val)
        },
        handleCurrentChange(val) {
            this.$emit('handleCurrentChange', val);
        },
        };
    },

    mounted() {
        
    },

    methods: {
        
    },
};
</script>

<style scoped>

</style>