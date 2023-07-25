// { "name": "SolonAdminClient", "baseUrl": "http://192.168.31.107:8889", "metadata": "demo", "status": 0, "lastHeartbeat": 1683981864211 }

export class UniqueApplication {
    name: string
    baseUrl: string

    constructor(name: string, baseUrl: string) {
        this.name = name;
        this.baseUrl = baseUrl;
    }

    toString(): string {
        return `${this.name}/${this.baseUrl}`
    }
}

export class Application extends UniqueApplication {
    metadata?: string
    status: ApplicationStatus
    lastHeartbeat: number
    startupTime: number
    lastUpTime: number
    lastDownTime: number
    showSecretInformation: boolean
    environmentInformation: EnvironmentInformation
    monitors?: (JVMDetector | DiskDetector | CPUDetector | OSDetector | MemoryDetector | Detector)[]

    constructor(name: string, baseUrl: string, metadata: string | undefined, status: ApplicationStatus, lastHeartbeat: number, startupTime: number, lastUpTime: number, lastDownTime: number, showSecretInformation: boolean, environmentInformation: EnvironmentInformation, monitors: Detector[]) {
        super(name, baseUrl);
        this.metadata = metadata;
        this.status = status;
        this.lastHeartbeat = lastHeartbeat;
        this.startupTime = startupTime;
        this.lastUpTime = lastUpTime;
        this.lastDownTime = lastDownTime;
        this.showSecretInformation = showSecretInformation;
        this.environmentInformation = environmentInformation;
        this.monitors = monitors;
    }
}

export enum ApplicationStatus {
    UP = 0,
    DOWN = 1,
}

export type ApplicationWebSocketTransfer<T> = {
    scope: UniqueApplication | undefined | null,
    type: string,
    data: T
}

export type EnvironmentInformation = {
    systemEnvironment: {
        [key: string]: string
    };
    systemProperties: {
        [key: string]: string
    };
    applicationProperties: {
        [key: string]: string
    };
}

export type Detector = {
    name: string
    info: {
        [key: string]: object | number | string
    }
}

export type OSDetector = {
    name: 'os',
    info: {
        name: string,
        arch: string,
        version: string,
    }
}

export type MemoryDetector = {
    name: 'memory',
    info: {
        total: string,
        used: string,
        ratio: number
    }
}

export type CPUDetector = {
    name: 'cpu',
    info: {
        ratio: number
    }
}

export type DiskDetector = {
    name: 'disk',
    info: {
        total: string,
        used: string,
        details: {
            [key: string]: {
                total: string,
                used: string,
                free: string,
                ratio: string
            }
        }
    }
}

export type JVMDetector = {
    name: 'jvm',
    info: {
        ratio: number,
        total: string,
        used: string,
        usable: string,
        free: string
    }
}
