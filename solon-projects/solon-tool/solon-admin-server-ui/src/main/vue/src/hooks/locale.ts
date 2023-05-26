import {computed} from 'vue';
import {useI18n} from 'vue-i18n';
import enUS from '@arco-design/web-vue/es/locale/lang/en-us';
import zhCN from '@arco-design/web-vue/es/locale/lang/zh-cn';
import {Message} from "@arco-design/web-vue";

export default function useLocale() {
    const i18n = useI18n();
    const allLocales = i18n.messages;
    const currentLocale = computed(() => {
        const customLocale = allLocales.value[i18n.locale.value];
        switch (i18n.locale.value) {
            case 'en_US':
                return {
                    ...enUS,
                    ...customLocale
                };
            case 'zh_CN':
            default:
                return {
                    ...zhCN,
                    ...customLocale
                };
        }
    });
    const changeLocale = (value: string) => {
        i18n.locale.value = value;
        localStorage.setItem('solon-admin-locale', value);
        Message.success(i18n.t('header.action.locale', {language: i18n.t('language')}));
    };
    return {
        currentLocale,
        changeLocale,
        allLocales,
    };
}