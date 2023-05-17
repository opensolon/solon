import {createRouter, createWebHashHistory} from 'vue-router'
import {routes} from "./route.ts";

export const router = createRouter({
    history: createWebHashHistory(),
    routes
})
declare module 'vue-router' {
    interface RouteMeta {
        showInHeader?: boolean,
        showSideBar?: boolean,
        ignored?: boolean,
        index?: number
    }
}