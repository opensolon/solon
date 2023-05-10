<script lang="ts" setup>
import useLocale from "./hooks/locale.ts";

const {currentLocale} = useLocale();
</script>

<template>
    <a-config-provider :locale="currentLocale">
        <router-view/>
    </a-config-provider>
</template>

<style scoped>

</style>
