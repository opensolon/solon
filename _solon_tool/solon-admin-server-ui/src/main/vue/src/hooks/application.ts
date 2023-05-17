import {computedAsync} from "@vueuse/core";
import {Application} from "../data";
import {ref, watch} from "vue";
import {Message} from "@arco-design/web-vue";
import {useI18n} from 'vue-i18n';

export function useApplications() {

    const i18n = useI18n();

    const time = ref(new Date().getTime())
    const applications = ref<Application[]>([])
    const isEvaluating = ref(true);

    const evaluatingApplications = computedAsync<Application[]>(async () => {
        return await fetch("/api/application/all?" + time.value)
            .then(response => response.json())
    }, null!!, isEvaluating)
    watch(evaluatingApplications, (value) => {
        applications.value = value
    })

    const updateApplications = () => {
        time.value = new Date().getTime()
    }

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
        updateApplications,
        unregisterApplication
    };
}