import {createApp} from 'vue'
import './style.css'
import App from './App.vue'
import {router} from "./route/router.ts";
import {i18n} from "./locale";
import {pinia} from "./store";

createApp(App)
    .use(router)
    .use(i18n)
    .use(pinia)
    .mount('#app')
