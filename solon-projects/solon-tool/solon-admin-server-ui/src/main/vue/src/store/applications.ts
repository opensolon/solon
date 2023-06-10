import {defineStore, StoreDefinition} from 'pinia'
import {UniqueApplication} from "../data";

const applicationStores: Map<string, StoreDefinition> = new Map();

export function useApplicationStore(application: UniqueApplication) {
    const value = applicationStores.get(application.toString())

    if (value === undefined) {
        const store = generateStore(application)
        applicationStores.set(application.toString(), store)
        return store
    }

    return value
}

function generateStore(application: UniqueApplication): StoreDefinition {
    return defineStore(application.toString(), () => {

    })
}