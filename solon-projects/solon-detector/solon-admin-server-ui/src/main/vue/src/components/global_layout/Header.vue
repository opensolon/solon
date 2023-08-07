<script lang="ts" setup>
import icon from '../../assets/icon.png'
import {computed, ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";
import useLocale from "../../hooks/locale.ts";
import {useI18n} from "vue-i18n";

const router = useRouter()
const route = useRoute()
const {changeLocale, allLocales} = useLocale();
const {t} = useI18n()

const selectedKeys = ref<string[]>()

const path = computed(() => route.name as string)
watch(path, (value) => {
    selectedKeys.value = [value];
})

const isHome = computed(() => {
    return path.value === "home"
})

function onClickMenuItem(key: string) {
    if (key.startsWith("_ignore:")
        || router.getRoutes().find(r => r.name === key)?.meta?.ignored === true) {
        selectedKeys.value = [path.value]
        return
    }
    router.push({name: key})
}

</script>

<template>
    <div class="header">
        <a-menu v-model:selected-keys="selectedKeys" mode="horizontal" @menu-item-click="onClickMenuItem">
            <a-menu-item key="_ignore:icon" disabled>
                <div id="icon">
                    <img :src="icon" alt="icon"/>
                    <span>{{ t("header.title") }}</span>
                </div>
            </a-menu-item>
            <template
                v-for="item in router.getRoutes()
                .filter(it=>it.meta.showInHeader!=false)
                .filter(it=> isHome ? it.meta.showInHome!=false : true)
                .sort((a,b)=>(a.meta.index==undefined?0:a.meta.index) - (b.meta.index==undefined?0:b.meta.index))"
                :key="item.name">
                <a-menu-item>{{ t(`header.item.${String(item.name)}`) }}</a-menu-item>
            </template>
            <a-sub-menu class="language-menu">
                <template #title>{{ t("language") }}</template>
                <template v-for="language in Object.keys(allLocales)" :key="'_ignore:'+language">
                    <a-menu-item @click="() => changeLocale(language)">{{ allLocales[language].language }}</a-menu-item>
                </template>
            </a-sub-menu>
        </a-menu>
    </div>
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
