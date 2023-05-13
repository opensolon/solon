import {computedAsync} from "@vueuse/core";
import {Application} from "../data";
import {Ref} from "vue-demi";
import {ref} from "vue";

export default function useApplication() {
    const getAllApplications = (isEvaluating?: Ref<boolean>) => {
        let time = ref(new Date().getTime())
        const applications = computedAsync<Application[]>(async () => {
            return await fetch("/api/application/all?" + time.value)
                .then(response => response.json())
        }, null!!, isEvaluating)
        const updateApplications = () => {
            time.value = new Date().getTime()
        }
        return {applications, updateApplications}
    }
    return {
        getAllApplications
    };
}