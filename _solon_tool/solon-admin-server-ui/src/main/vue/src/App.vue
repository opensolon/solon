<script lang="ts" setup>
import GlobalLayout from "./components/./global_layout/index.vue";

import useLocale from "./hooks/locale.ts";

const {currentLocale} = useLocale();
</script>

<template>
    <a-config-provider :locale="currentLocale">
        <global-layout>
            <router-view/>
        </global-layout>
    </a-config-provider>
</template>

<style scoped>

</style>
