<script lang="ts" setup>
import {useApplication} from "../../hooks/application.ts";
import {useI18n} from "vue-i18n";
import {computed} from "vue";
import {ApplicationStatus} from "../../data";
import {useMediaQuery} from "@vueuse/core";

const {t} = useI18n()

const application = useApplication().currentApplication()

const localizedStatus = computed(() => {
  return {
    content: t(application?.value?.status == ApplicationStatus.UP ? "online" : "offline"),
    color: application?.value?.status == ApplicationStatus.UP ? "green" : "red"
  }
})

const isLargeScreen = useMediaQuery('(min-width: 1024px)')

function getLocalizedTime(time: number | undefined) {
  return time && time >= 0 ? new Date(time) : t('never')
}

</script>

<template>
  <div class="top-bar">
    <div class="title">
      <h1>{{ application?.name }}</h1>
    </div>
  </div>
  <div class="dashboard">
    <a-grid :colGap="12" :cols="isLargeScreen ? 2 : 1" :rowGap="16">
      <a-grid-item>
        <a-card :title="t('application.details.dashboard.info.title')" class="card">
          <div class="card-row">
            <span>{{ t('application.details.dashboard.info.application_name') }}: </span>
            <a-tag color="blue">{{ application?.name }}</a-tag>
          </div>
          <div class="card-row">
            <span>{{ t('application.details.dashboard.info.application_url') }}: </span>
            <a-tag color="blue"><a :href="application?.baseUrl">{{ application?.baseUrl }}</a></a-tag>
          </div>
          <div class="card-row">
            <span>{{ t('application.details.dashboard.info.startup_time') }}: </span>
            <a-tag color="blue">{{ getLocalizedTime(application?.startupTime) }}
            </a-tag>
          </div>
          <div class="card-row">
            <span>{{ t('application.details.dashboard.info.last_heartbeat_time') }}: </span>
            <a-tag color="blue">{{ getLocalizedTime(application?.lastHeartbeat) }}
            </a-tag>
          </div>
        </a-card>
      </a-grid-item>
      <a-grid-item>
        <a-card :title="t('application.details.dashboard.health.title')" class="card">
          <div class="card-row">
            <span>{{ t('application.details.dashboard.health.online_status') }}: </span>
            <a-tag :color="localizedStatus.color">{{ localizedStatus.content }}</a-tag>
          </div>
          <div class="card-row">
            <span>{{ t('application.details.dashboard.health.last_up_time') }}: </span>
            <a-tag color="blue">{{ getLocalizedTime(application?.lastUpTime) }}</a-tag>
          </div>
          <div class="card-row">
            <span>{{ t('application.details.dashboard.health.last_down_time') }}: </span>
            <a-tag color="blue">{{ getLocalizedTime(application?.lastDownTime) }}</a-tag>
          </div>
        </a-card>
      </a-grid-item>
      <a-grid-item>
        <a-card :title="t('application.details.dashboard.metadata.title')" class="card">
          <span v-if="application?.metadata">{{ application?.metadata }}</span>
          <a-empty v-else/>
        </a-card>
      </a-grid-item>
    </a-grid>
  </div>
</template>

<style scoped>
.top-bar {
  padding: 10px;
}

.top-bar .title {
  text-align: center;
  margin: auto;
}

.dashboard {
  box-sizing: border-box;
  width: 100%;
  padding: 40px;
  background-color: var(--color-fill-2);
}

.card :deep(.arco-card-body) {
  line-height: 30px;
}

.card .card-row {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}
</style>