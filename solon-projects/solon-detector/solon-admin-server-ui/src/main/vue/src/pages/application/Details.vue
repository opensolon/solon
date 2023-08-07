<script lang="ts" setup>
import {useApplication} from "../../hooks/application.ts";
import {useI18n} from "vue-i18n";
import {computed, ComputedRef, nextTick, onMounted, reactive, watchEffect} from "vue";
import {ApplicationStatus, CPUDetector, DiskDetector, JVMDetector, MemoryDetector} from "../../data";

import {use} from "echarts/core";
import {GridComponent} from 'echarts/components';
import {GaugeChart, LineChart} from 'echarts/charts';
import {UniversalTransition} from 'echarts/features';
import {CanvasRenderer} from 'echarts/renderers';
import {ECharts, EChartsOption} from "echarts";
import {formatToByteSize} from "../../utils";
import {DescData} from "@arco-design/web-vue";

use([
  GridComponent,
  GaugeChart,
  LineChart,
  CanvasRenderer,
  UniversalTransition
]);

const {t} = useI18n()

const application = useApplication().currentApplication()

const localizedStatus = computed(() => {
  return {
    content: t(application?.value?.status == ApplicationStatus.UP ? "online" : "offline"),
    color: application?.value?.status == ApplicationStatus.UP ? "green" : "red"
  }
})

function getLocalizedTime(time: number | undefined) {
  return time && time >= 0 ? new Date(time) : t('never')
}

const charts = reactive<{
  [key: string]: ECharts
}>({})

const savedMemoryData = reactive<{
  [time: number]: number
}>({})

const savedJVMData = reactive<{
  [time: number]: number
}>({})

watchEffect(() => {
  application.value?.monitors?.forEach(monitor => {
    if (monitor.name === 'memory') {
      savedMemoryData[new Date().getTime()] = formatToByteSize((monitor as MemoryDetector).info.used) / 1024 / 1024 / 1024

      const keys = Object.keys(savedMemoryData)
      if (Object.keys(savedMemoryData).length > 10) {
        delete savedMemoryData[keys[0]]
      }
    } else if (monitor.name === 'jvm') {
      savedJVMData[new Date().getTime()] = formatToByteSize((monitor as JVMDetector).info.used) / 1024 / 1024

      const keys = Object.keys(savedJVMData)
      if (Object.keys(savedJVMData).length > 10) {
        delete savedJVMData[keys[0]]
      }
    }
  })
})

const computedMemoryChartData = computed<EChartsOption>(() => {
  return {
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: Object.keys(savedMemoryData).map(it => new Date(Number(it)).toLocaleTimeString()),
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value} GB'
      },
      min: 0,
      max: formatToByteSize((application.value?.monitors?.find(it => it.name === 'memory') as MemoryDetector | undefined)?.info?.total ?? "0GB") / 1024 / 1024 / 1024,
    },
    series: [
      {
        data: Object.values(savedMemoryData),
        type: 'line',
        areaStyle: {}
      }
    ]
  }
})

const computedJVMChartData = computed<EChartsOption>(() => {
  return {
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: Object.keys(savedJVMData).map(it => new Date(Number(it)).toLocaleTimeString()),
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value} MB'
      },
      min: 0,
      max: formatToByteSize((application.value?.monitors?.find(it => it.name === 'jvm') as JVMDetector | undefined)?.info?.total ?? "0MB") / 1024 / 1024,
    },
    series: [
      {
        data: Object.values(savedJVMData),
        type: 'line',
        areaStyle: {}
      }
    ]
  }
})

const computedCPUChartData = computed<EChartsOption>(() => {
  return {
    series: [
      {
        name: 'CPU Usage',
        type: 'gauge',
        progress: {
          show: true
        },
        detail: {
          valueAnimation: true,
          formatter: function (value: number) {
            return value.toFixed(2) + '%';
          }
        },
        data: [
          {
            value: (application.value?.monitors?.find(it => it.name === 'cpu') as CPUDetector | undefined)?.info?.ratio ?? 0,
            name: t('application.details.dashboard.cpu.ratio.title')
          }
        ]
      }
    ]
  }
})


function getDiskComputedData(): ComputedRef<{
  title: string,
  data: DescData[]
}[]> {
  return computed(() => {
    const data = (application?.value?.monitors?.find(it => it.name === 'disk') as DiskDetector | undefined)?.info
    const result: {
      title: string,
      data: DescData[]
    }[] = []
    result.push({
      title: t(`application.details.dashboard.disk.all.title`),
      data: [
        {
          label: t(`application.details.dashboard.disk.total.title`),
          value: data?.total ?? ""
        },
        {
          label: t(`application.details.dashboard.disk.used.title`),
          value: data?.used ?? ""
        }
      ]
    })
    for (let disk in data?.details) {
      const detail: DescData[] = []
      for (let info in data?.details[disk]) {
        detail.push({
          label: t(`application.details.dashboard.disk.${info}.title`),
          value: data?.details[disk][info]
        })
      }
      result.push({
        title: disk,
        data: detail,
      })
    }
    return result;
  })
}

function getComputedData(name: string): ComputedRef<{
  label: string,
  value: string
}[]> {
  return computed(() => {
    const data = application?.value?.monitors?.find(it => it.name === name)?.info
    const result: {
      label: string,
      value: string
    }[] = []
    for (let info in data) {
      result.push({
        label: t(`application.details.dashboard.${name}.${info}.title`),
        value: data[info] as string
      })
    }
    return result;
  })
}

function resizeAll() {
  for (let chart in charts) {
    resize(charts[chart])
  }
}

function resize(chart: ECharts) {
  chart.resize({
    width: chart?.getDom()?.parentElement?.clientWidth,
    height: chart?.getDom()?.parentElement?.clientHeight
  })
}

window.addEventListener('resize', () => {
  resizeAll()
})

onMounted(() => {
  nextTick(() => {
    resizeAll()
  })
})

</script>

<template>
  <a-alert v-if="!application?.monitors?.length"
           :title="t('application.details.alert.no_available_monitor.title')"
           closable
           type="warning">
    {{ t("application.details.alert.no_available_monitor.content") }}
  </a-alert>
  <div class="top-bar">
    <div class="title">
      <h1>{{ application?.name }}</h1>
    </div>
  </div>
  <div class="dashboard">
    <div>
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
    </div>
    <div>
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
    </div>
    <div>
      <a-card :title="t('application.details.dashboard.metadata.title')" class="card">
        <span v-if="application?.metadata">{{ application?.metadata }}</span>
        <a-empty v-else/>
      </a-card>
    </div>
    <div v-for="monitor in application?.monitors?.sort((a,b)=>a.name.charCodeAt(0)-b.name.charCodeAt(0))"
         :key="monitor.name">
      <a-card :title="t(`application.details.dashboard.${monitor.name}.title`)" class="card">
        <template v-if="monitor.name==='memory'">
          <a-descriptions :data="getComputedData('memory').value" size="large" style="margin-top: 20px"/>
          <div>
            <v-chart :ref="it => charts[monitor.name] = it"
                     :autoresize="{onResize: ()=> resize(charts[monitor.name])}"
                     :option="computedMemoryChartData"
                     class="chart"/>
          </div>
        </template>
        <template v-else-if="monitor.name==='jvm'">
          <a-descriptions :data="getComputedData('jvm').value" size="large" style="margin-top: 20px"/>
          <div>
            <v-chart :ref="it => charts[monitor.name] = it"
                     :autoresize="{onResize: ()=> resize(charts[monitor.name])}"
                     :option="computedJVMChartData"
                     class="chart"/>
          </div>
        </template>
        <template v-else-if="monitor.name==='cpu'">
          <div>
            <v-chart :ref="it => charts[monitor.name] = it"
                     :autoresize="{onResize: ()=> resize(charts[monitor.name])}"
                     :option="computedCPUChartData"
                     class="chart"/>
          </div>
        </template>
        <template v-else-if="monitor.name==='os' || monitor.name=='qps'">
          <a-descriptions :data="getComputedData(monitor.name).value" size="large" style="margin-top: 20px"/>
        </template>
        <template v-else-if="monitor.name==='disk'">
          <template v-for="data in getDiskComputedData().value">
            <a-descriptions :data="data.data" :title="data.title" size="large" style="margin-top: 20px"/>
          </template>
        </template>
        <template v-else>
          {{ monitor.info }}
        </template>
      </a-card>
    </div>
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
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
  gap: 16px;

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

.chart {
  height: 400px;
  width: 400px;
}
</style>
