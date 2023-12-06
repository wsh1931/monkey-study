<template>
    <div>
        <div 
        v-if="hottestResourceList.length > 0" 
        class="curation-card" 
        v-for="hottestResource in hottestResourceList" 
        :key="hottestResource.id"
        @click="toResourceDetail(hottestResource.id)">
            <div>
                <img class="curation-typeImg" :src="hottestResource.typeUrl" alt="">
            </div>
            <div style="text-align: center;">
                <span class="curation-achievement">
                    <span class="el-icon-view like">&nbsp;</span>
                    <span class="collect-count">{{ getFormatNumber(hottestResource.viewCount) }}
                        <span style="opacity: 0.2;">|&nbsp;</span>
                    </span>
                
                    <span class="el-icon-download like">&nbsp;</span>
                    <span class="collect-count">{{ getFormatNumber(hottestResource.downCount) }}</span>
                </span>
            </div>
            <div class="curation-name">
                {{ hottestResource.name }}
            </div>
            <div style="text-align: center;">
                <div class="curation-fee" v-if="hottestResource.formTypeId == '1'">
                    免费
                </div>
                <div class="curation-vip" v-if="hottestResource.formTypeId == '2'">
                    vip
                </div>
                <div class="curation-price" v-if="hottestResource.formTypeId == '3'">
                    ￥{{ hottestResource.price }}
                </div>
            </div>
        </div>
        <div v-if="hottestResourceList == null || hottestResourceList == '' || hottestResourceList == []">
            <el-empty description="暂无资源"></el-empty>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import { getFormatNumber } from '@/assets/js/NumberMethod'
export default {
    name: 'MonkeyWebDownMoreCard',
    props: ['hottestResourceList'],
    data() {
        return {
            
        };
    },
    methods: {
        // 前往资源详情页面
        toResourceDetail(resourceId) {
            const { href } = this.$router.resolve({
                name: "resource_detail",
                params: {
                    resourceId
                }
            })

            window.open(href, "_blank")
        },
        getFormatNumber(numbers) {
            return getFormatNumber(numbers);
        },
    },
};
</script>

<style scoped>
.el-icon-download {
    font-size: 16px;
}
.el-icon-view {
    font-size: 16px;
}
.curation-price {
    position: absolute;
    top: 28px;
    left: 48px;
    display: inline-block;
    color: #fff;
    background-color: #f79708;
    text-align: center;
    padding: 2px 5px;
    border-radius: 5px;
}
.curation-vip {
    position: absolute;
    top: 28px;
    left: 48px;
    display: inline-block;
    padding: 2px 5px;
    text-align: center;
    background-color: #f90a0a;
    color: #fff;
    border-radius: 5px;
}
.curation-fee {
    position: absolute;
    top: 28px;
    left: 48px;
    display: inline-block;
    padding: 2px 5px;
    text-align: center;
    background-color: #00f2fe;
    color: #fff;
    border-radius: 5px;
}
.curation-name {
    text-align: center;
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    margin-bottom: 5px;
}
.collect-count {
    vertical-align: middle; 
}

.like {
    vertical-align: middle; 
}
.curation-achievement {
    font-size: 12px;
}

.curation-typeImg {
    width: 150px;
    height: 150px;
}

.curation-card {
    position: relative;
    display: inline-block;
    padding: 20px;
    transition: 0.3s linear all;
    cursor: pointer;
    width: 142px;
}
.curation-card:hover {
    box-shadow: 0 0 10px 0 grey;
}
</style>