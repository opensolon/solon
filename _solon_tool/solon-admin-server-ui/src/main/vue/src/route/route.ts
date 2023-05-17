import Home from "../pages/Home.vue";
import Application from "../pages/application/index.vue";
import {RouteRecordRaw} from "vue-router";
import Details from "../pages/application/Details.vue";
import NotFound from "../pages/NotFound.vue";

export const routes: RouteRecordRaw[] = [
    {
        path: '/',
        name: 'home',
        component: Home
    },
    {
        path: '/application/name/:name/baseUrl/:baseUrl',
        component: Application,
        children: [
            {
                name: 'details',
                path: '',
                component: Details,
                meta: {
                    showInHeader: true,
                    ignored: true,
                    index: 1
                }
            }
        ],
        meta: {
            showInHeader: false
        }
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: NotFound,
        meta: {
            showInHeader: false
        }
    },
]