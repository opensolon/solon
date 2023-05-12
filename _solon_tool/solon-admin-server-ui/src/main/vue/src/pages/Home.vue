<script lang="ts" setup>

import {useI18n} from "vue-i18n";

const {t} = useI18n()

const bool = true;

</script>

<template>
    <div class="dashboard">
        <div class="empty" v-if="bool">
            <a-empty :description="t('home.getting-started')">
                <template #image>
                    <icon-info-circle-fill/>
                </template>
            </a-empty>
        </div>
        <div class="items" v-else>
            <a-list>
                <template #header>
                    List title
                </template>
                <a-list-item>Beijing Bytedance Technology Co., Ltd.</a-list-item>
                <a-list-item>Bytedance Technology Co., Ltd.</a-list-item>
                <a-list-item>Beijing Toutiao Technology Co., Ltd.</a-list-item>
                <a-list-item>Beijing Volcengine Technology Co., Ltd.</a-list-item>
                <a-list-item>China Beijing Bytedance Technology Co., Ltd.</a-list-item>
            </a-list>
        </div>
    </div>
</template>

<style scoped>
.dashboard {
    display: flex;

    min-height: 50vh;
}

.empty {
    margin: auto;
}

.items{
    width: 100%;
}
</style>