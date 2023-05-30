import {Application, ApplicationWebSocketTransfer} from "../data";
import {ref} from "vue";
import {Message} from "@arco-design/web-vue";
import {useI18n} from 'vue-i18n';

export function useApplications() {

    const i18n = useI18n();

    const applications = ref<Application[]>([])
    const isEvaluating = ref(true);

    const websocket = new WebSocket("ws://" + window.location.host + "/ws/application");
    websocket.addEventListener('message', (event: MessageEvent) => {
        const data = JSON.parse(event.data) as ApplicationWebSocketTransfer<any>
        switch (data.type) {
            case "getAllApplication":
                applications.value = data.data as Application[];
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
        websocket.send(JSON.stringify({
            type: "getAllApplication"
        }))
    })

    const unregisterApplication = async (application: Application) => {
        const isOk = await fetch("/api/application/unregister", {
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
        applications.value = applications.value.filter(app => app.name !== application.name || app.baseUrl !== application.baseUrl)
        Message.success(i18n.t('home.applications.actions.remove.success'));
    }

    return {
        applications,
        isEvaluating,
        unregisterApplication
    };
}

export function useApplication() {

    const getApplication = async (name: string, baseUrl: string) => {
        return await fetch("/api/application?name=" + name + "&baseUrl=" + baseUrl)
            .then(response => response.json())
            .then(response => response as Application)
    }

    return {
        getApplication
    }
}