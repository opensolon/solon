<script lang="ts" setup>
import GlobalLayout from "./components/global_layout/index.vue";

import useLocale from "./hooks/locale.ts";
import {useRoute} from "vue-router";

const {currentLocale} = useLocale();

const route = useRoute()
</script>

<template>
    <a-config-provider :locale="currentLocale">
        <global-layout>
            <template #default>
              <router-view/>
            </template>
            <template #sidebar v-if="route.meta.showSideBar==true">
                <router-view name="sidebar"/>
            </template>
        </global-layout>
    </a-config-provider>
</template>

<style scoped>

</style>
