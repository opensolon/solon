import {Application, ApplicationWebSocketTransfer, UniqueApplication} from "../data";
import {computed, reactive, Ref, ref} from "vue";
import {Message, Notification} from "@arco-design/web-vue";
import {useI18n} from 'vue-i18n';
import {useRoute} from "vue-router";

const applications = ref<Application[]>([])
const isEvaluating = ref(true);
let websocket: WebSocket | undefined = undefined;

export function useApplications() {

    const i18n = useI18n();

    function connect() {
        if (websocket !== undefined) return

        websocket = new WebSocket("ws://" + window.location.host + "/ws/application");

        websocket.addEventListener('message', (event: MessageEvent) => {
            const data = JSON.parse(event.data) as ApplicationWebSocketTransfer<any>
            switch (data.type) {
                case "getAllApplication":
                    applications.value = reactive(data.data as Application[]);
                    isEvaluating.value = false;
                    break
                case "registerApplication":
                    applications.value.push(data.data as Application)
                    break
                case "unregisterApplication":
                    applications.value = applications.value.filter(app => app.name !== data.data.name || app.baseUrl !== data.data.baseUrl)
                    break;
                case "updateApplication":
                    applications.value = applications.value.map(app => {
                        if (app.name === data.data.name && app.baseUrl === data.data.baseUrl) {
                            return data.data as Application
                        }
                        return app
                    })
            }
        })
        websocket.addEventListener('open', () => {
            if (websocket === undefined) return

            websocket.send(JSON.stringify({
                type: "getAllApplication"
            }))
        })

        websocket.addEventListener('close', () => {
            websocket = undefined
            isEvaluating.value = true;

            Notification.error(i18n.t('websocket.disconnect'))
        })
    }

    connect()

    return {
        applications,
        isEvaluating
    };
}

export function useApplication() {

    const applications = useApplications().applications;
    const route = useRoute()
    const i18n = useI18n();

    const unregisterApplication = async (application: Application) => {
        const isOk = await fetch("/solon-admin/api/application/unregister", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(application)
        }).then(res => res.ok)
        if (!isOk) {
            Message.success(i18n.t('home.applications.actions.remove.fail'));
            return
        }
        Message.success(i18n.t('home.applications.actions.remove.success'));
    }

    const getApplicationRaw = async (name: string, baseUrl: string) => {
        return await fetch("/solon-admin/api/application?name=" + encodeURIComponent(name) + "&baseUrl=" + encodeURIComponent(baseUrl))
            .then(response => response.json())
            .then(response => response as Application)
    }

    const getApplication = (application: Ref<UniqueApplication>) => {
        return computed(() => applications.value.find(app => app.name === application.value.name && app.baseUrl === application.value.baseUrl))
    }

    function currentApplication() {
        return useApplication().getApplication(computed(() => new UniqueApplication(decodeURIComponent(route.params.name as string), decodeURIComponent(route.params.baseUrl as string))))
    }

    return {
        currentApplication,
        getApplication,
        getApplicationRaw,
        unregisterApplication
    }
}