<script lang="ts" setup>
import Header from "./Header.vue";
import {useSlots} from "vue";

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
            <a-layout-sider v-if="hasSlot('sider')">
                <slot name="sider"/>
            </a-layout-sider>
            <a-layout-content v-if="hasSlot('default')">
                <slot/>
            </a-layout-content>
        </a-layout>
        <a-layout-footer v-if="hasSlot('footer')">
            <slot name="footer"/>
        </a-layout-footer>
    </a-layout>
</template>

<style scoped>

</style>