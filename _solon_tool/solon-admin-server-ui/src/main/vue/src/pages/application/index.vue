<script lang="ts" setup>
import {onBeforeRouteUpdate, useRoute} from "vue-router";
import {useApplication} from "../../hooks/application.ts";
import {Application} from "../../data";
import {useAsyncState} from "@vueuse/core";

const route = useRoute()
const {state: application} = useAsyncState<Application | null>(
    useApplication().getApplication(encodeURIComponent(route.params.name as string), encodeURIComponent(route.params.baseUrl as string)),
    null,
)

onBeforeRouteUpdate(async (to) => {
    application.value = await useApplication().getApplication(encodeURIComponent(to.params.name as string), encodeURIComponent(to.params.baseUrl as string))
})
</script>

<template>
    <router-view/>
</template>

<style scoped>

</style>