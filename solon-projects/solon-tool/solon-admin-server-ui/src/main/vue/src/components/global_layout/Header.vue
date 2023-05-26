<script lang="ts" setup>
import icon from '../../assets/icon.png'
import {routes} from "../../route/route.ts";
import {computed, ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import useLocale from "../../hooks/locale.ts";

const router = useRouter()
const route = useRoute()
const {changeLocale, allLocales} = useLocale();

const path = computed(() => [route.name as string])
watch(path, (value) => {
    currentPath.value = value;
})

const currentPath = ref<string[]>()
watch(currentPath, (value, oldValue) => {
    if (value == oldValue || !value) return
    if (value[0].startsWith("_ignore:")) {
        currentPath.value = oldValue
        return
    }
    router.push({name: value[0]})
})
</script>

<template>
    <a-menu v-model:selected-keys="currentPath" mode="horizontal">
        <a-menu-item key="_ignore:icon" disabled>
            <div id="icon">
                <img :src="icon" alt="icon"/>
                <span>{{ $t("header.title") }}</span>
            </div>
        </a-menu-item>
        <template v-for="item in routes" :key="item.name">
            <a-menu-item>{{ $t(`header.item.${item.name}`) }}</a-menu-item>
        </template>
        <a-sub-menu>
            <template #title>{{ $t("language") }}</template>
            <template v-for="language in Object.keys(allLocales)" :key="'_ignore:'+language">
                <a-menu-item @click="() => changeLocale(language)">{{ allLocales[language].language }}</a-menu-item>
            </template>
        </a-sub-menu>
    </a-menu>
</template>

<style scoped>
#icon {
    display: flex;

    height: 30px;
    cursor: default;
    border-radius: 2px;

    gap: 5px;
}

#icon img {
    height: 100%;
}
</style>