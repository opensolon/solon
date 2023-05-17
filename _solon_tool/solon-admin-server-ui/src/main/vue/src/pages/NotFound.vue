<script lang="ts" setup>
import {useRouter} from "vue-router";
import {useI18n} from "vue-i18n";

const {t} = useI18n()

const router = useRouter()

function back() {
    router.back()
}
</script>

<template>
    <a-result :subtitle="t('notfound.subtitle')" status="404">
        <template #extra>
            <a-space>
                <a-button type="primary" @click="back">{{ t('notfound.extra.action.back') }}</a-button>
            </a-space>
        </template>
    </a-result>
</template>

<style scoped>
.arco-result {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
}
</style>