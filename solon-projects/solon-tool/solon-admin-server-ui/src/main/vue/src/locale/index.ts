import {createI18n} from "vue-i18n";
import en_us from "./en_us";
import zh_cn from "./zh_cn";

const locale = localStorage.getItem('solon-admin-locale') || "zh_CN";

export const i18n = createI18n({
    locale,
    fallbackLocale: "en_US",
    allowComposition: true,
    legacy: false,
    messages: {
        'en_US': en_us,
        'zh_CN': zh_cn,
    }
})