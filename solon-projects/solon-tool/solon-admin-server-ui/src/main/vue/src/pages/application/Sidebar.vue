<script lang="ts" setup>
import {useRoute, useRouter} from "vue-router";
import {useI18n} from "vue-i18n";
import {computed, ref, watch} from "vue";

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

</script>

<template>
  <div class="sidebar">
    <a-menu v-model:selected-keys="selectedKeys" mode="vertical" @menu-item-click="onClickMenuItem">
      <template
          v-for="item in (router.getRoutes().find(it=>it.name==='application')?.children??[])
                .filter(it=>it.meta?.showInHeader!=false)
                .sort((a,b)=>(a.meta?.index??0) - (b.meta?.index??0))"
          :key="item.name">
        <a-menu-item>{{ t(`sidebar.item.application.${String(item.name)}`) }}</a-menu-item>
      </template>
    </a-menu>
  </div>
</template>

<style scoped>

</style>