<script lang="ts" setup>
import {useRoute, useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import {computed, ref, watch} from "vue";
import {useApplication} from "../../hooks/application.ts";

const router = useRouter()
const route = useRoute()
const {t} = useI18n()

const selectedKeys = ref<string[]>()

const path = computed(() => route.name as string)
watch(path, (value) => {
  selectedKeys.value = [value];
})

function onClickMenuItem(key: string) {
  router.push({name: key})
}

const application = useApplication().currentApplication()
</script>

<template>
  <div class="sidebar">
    <div class="instance-name">
      <span>{{ application?.name }}</span>
    </div>
    <a-menu v-model:selected-keys="selectedKeys" mode="vertical" @menu-item-click="onClickMenuItem">
      <template
          v-for="item in (router.getRoutes().find(it=>it.name==='application')?.children??[])
                .filter(it=>it.meta?.showInSideBar!=false)
                .sort((a,b)=>(a.meta?.index??0) - (b.meta?.index??0))"
          :key="item.name">
        <a-menu-item>{{ t(`sidebar.item.application.${String(item.name)}`) }}</a-menu-item>
      </template>
    </a-menu>
  </div>
</template>

<style scoped>
.instance-name {
  padding: 1rem 1.5rem;
  margin: auto;
  text-align: center;
}
</style>