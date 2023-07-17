<template>
    <div class="MonkeyWebCourseCenterViews-container" style="width: 1100px; margin: 10px auto;">
        <el-row>
            <CourseLabelList/>
        </el-row>
        <el-row>

        </el-row>
    </div>
</template>

<script>
import CourseLabelList from '@/components/course/CourseLabelList.vue';
export default {
    name: 'MonkeyWebCourseCenterViews',
    components: {
        CourseLabelList
    },

    data() {
        return {
            
        };
    },

    methods: {
        
    },
};
</script>

<style lang="scss" scoped>

</style>