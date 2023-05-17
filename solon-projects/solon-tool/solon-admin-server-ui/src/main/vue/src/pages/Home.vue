<script lang="ts" setup>

import {useI18n} from "vue-i18n";
import {Application, ApplicationStatus} from "../data";
import {computed, onUnmounted} from "vue";
import {useApplications} from "../hooks/application.ts";
import {useRouter} from "vue-router";

const router = useRouter()

const {t} = useI18n()

const {applications, isEvaluating: isLoading, updateApplications, unregisterApplication} = useApplications()

const isEmpty = computed(() => applications.value.length === 0)
const isAbnormal = computed(() => applications.value.some(app => app.status !== ApplicationStatus.UP))

const timer = setInterval(() => {
    updateApplications()
}, 10000)

onUnmounted(() => {
    clearInterval(timer)
})

function goToDetails(app: Application) {
    router.push({
        name: 'details',
        params: {
            name: encodeURIComponent(app.name),
            baseUrl: encodeURIComponent(app.baseUrl)
        }
    })
}

</script>

<template>
    <div class="dashboard">
        <div class="summary">
            <div v-if="isLoading" class="loading">
                <div>
                    <a-spin :tip="t('loading')"/>
                </div>
            </div>
            <div v-else-if="isEmpty" class="empty">
                <a-empty :description="t('home.getting-started')">
                    <template #image>
                        <icon-info-circle-fill/>
                    </template>
                </a-empty>
            </div>
            <div v-else-if="isAbnormal" class="abnormal">
                <a-empty :description="t('home.summary.abnormal')">
                    <template #image>
                        <icon-exclamation-circle-fill style="color: rgb(var(--red-6))"/>
                    </template>
                </a-empty>
            </div>
            <div v-else class="normal">
                <a-empty :description="t('home.summary.normal')">
                    <template #image>
                        <icon-check-circle-fill style="color: rgb(var(--green-6))"/>
                    </template>
                </a-empty>
            </div>
        </div>
        <div v-if="!isEmpty" class="applications">
            <a-list>
                <template #header>
                    <span>{{ t('home.applications.title', {count: applications.length}) }}</span>
                </template>
                <template v-for="app in applications">
                    <a-list-item>
                        <a-list-item-meta :title="app.name">
                            <template #avatar>
                                <template v-if="app.status===ApplicationStatus.UP">
                                    <icon-check-circle-fill style="color: rgb(var(--green-6))"/>
                                </template>
                                <template v-else>
                                    <icon-exclamation-circle-fill style="color: rgb(var(--red-6))"/>
                                </template>
                            </template>
                            <template #description>
                                <a-link :href="app.baseUrl" icon>{{ app.baseUrl }}</a-link>
                            </template>
                        </a-list-item-meta>
                        <template #actions>
                            <div>
                                <a-popconfirm :content="t('home.applications.actions.remove.ask')"
                                              type="warning"
                                              @ok="unregisterApplication(app)">
                                    <a-tooltip :content="t('home.applications.actions.remove.tooltip')">
                                        <icon-delete/>
                                    </a-tooltip>
                                </a-popconfirm>
                            </div>
                            <div>
                                <a-tooltip :content="t('home.applications.actions.details.tooltip')">
                                    <icon-launch
                                        @click="goToDetails(app)"/>
                                </a-tooltip>
                            </div>
                        </template>
                    </a-list-item>
                </template>
            </a-list>
        </div>
    </div>

</template>

<style scoped>
.dashboard {
    margin: 2.0rem;
}

.summary {
    margin-bottom: 1.5rem;
    padding: 20px;

    border: darkgrey 1px solid;
    border-radius: 5px;
}

.loading div {
    display: inline;
    margin: auto;
}

:deep(.arco-empty-description) {
    color: BLACK;
    font-size: 1.5rem;
    line-height: 2rem;
    font-weight: 700;
}

:deep(.arco-empty-image) {
    font-size: 100px;
}

:deep(.arco-list-item-meta-avatar) {
    font-size: 30px;
}

:deep(.arco-list-item-meta-description) .arco-link {
    padding: 0;
}
</style>