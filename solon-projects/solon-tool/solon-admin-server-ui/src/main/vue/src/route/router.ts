import {createRouter, createWebHashHistory} from 'vue-router'
import {routes} from "./route.ts";

export const router = createRouter({
    history: createWebHashHistory(),
    routes
})
declare module 'vue-router' {
    interface RouteMeta {
        showSideBar?: boolean,
        showInHeader?: boolean,
        showInSideBar?: boolean,
        ignored?: boolean,
        index?: number
    }
}