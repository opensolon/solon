<script lang="ts" setup>
import {useApplication} from "../../hooks/application.ts";

const application = useApplication().currentApplication()

</script>

<template>
  <router-view/>
</template>

<style scoped>

</style>