<script lang="ts" setup>
import {useApplication} from "../../hooks/application.ts";

const application = useApplication().currentApplication()
</script>

<template>
  {{ application }}
</template>

<style scoped>

</style>