<script lang="ts" setup>

import Header from "./Header.vue";
</script>

<template>
    <a-layout>
        <a-layout-header>
            <Header/>
        </a-layout-header>
        <a-layout>
            <a-layout-sider>
                <slot name="sider"/>
            </a-layout-sider>
            <a-layout-content>
                <slot/>
            </a-layout-content>
        </a-layout>
        <a-layout-footer>
            <slot name="footer"/>
        </a-layout-footer>
    </a-layout>
</template>

<style scoped>

</style>