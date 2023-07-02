<script lang="ts" setup>
import Header from "./Header.vue";
import {useSlots} from "vue";
import Footer from "./Footer.vue";

const slots = useSlots()
const hasSlot = (name) => {
    return !!slots[name];
}
</script>

<template>
    <a-layout>
        <a-layout-header>
            <Header/>
        </a-layout-header>
        <a-layout>
            <a-layout-sider v-if="hasSlot('sidebar')" collapsible>
              <slot name="sidebar"/>
            </a-layout-sider>
            <a-layout-content v-if="hasSlot('default')">
                <slot/>
            </a-layout-content>
        </a-layout>
        <a-layout-footer>
            <Footer/>
        </a-layout-footer>
    </a-layout>
</template>

<style scoped>
.arco-layout {
    height: 100%;
    overflow: hidden;
}
</style>