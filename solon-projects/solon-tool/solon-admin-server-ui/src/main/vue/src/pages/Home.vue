<script lang="ts" setup>

import {useI18n} from "vue-i18n";

const {t} = useI18n()

</script>

<template>
    <div class="dashboard">
        <div class="empty">
            <a-empty :description="t('home.getting-started')">
                <template #image>
                    <icon-info-circle-fill/>
                </template>
            </a-empty>
        </div>
    </div>
</template>

<style scoped>
.dashboard {
    display: flex;

    min-height: 50vh;
    border-radius: 5px;
}

.empty {
    margin: auto;
}

.empty {
}
</style>