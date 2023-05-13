// { "name": "SolonAdminClient", "baseUrl": "http://192.168.31.107:8889", "metadata": "demo", "status": 0, "lastHeartbeat": 1683981864211 }
export type Application = {
    name: string,
    baseUrl: string,
    metadata?: string,
    status: ApplicationStatus,
    lastHeartbeat: number,
}

export enum ApplicationStatus {
    UP = 0,
    DOWN = 1,
}