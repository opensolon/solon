const ONE_KB = 1024;
const ONE_MB = ONE_KB * 1024;
const ONE_GB = ONE_MB * 1024;
const ONE_TB = ONE_GB * 1024;
const ONE_PB = ONE_TB * 1024;
const ONE_EB = ONE_PB * 1024;

export function formatFromByteSize(byteSize: number): string {
    if (byteSize >= ONE_EB)
        return (Math.round(byteSize / ONE_EB * 100.0) / 100.0) + "EB";
    if (byteSize >= ONE_PB)
        return (Math.round(byteSize / ONE_PB * 100.0) / 100.0) + "PB";
    if (byteSize >= ONE_TB)
        return (Math.round(byteSize / ONE_TB * 100.0) / 100.0) + "TB";
    if (byteSize >= ONE_GB)
        return (Math.round(byteSize / ONE_GB * 100.0) / 100.0) + "GB";
    if (byteSize >= ONE_MB)
        return (Math.round(byteSize / ONE_MB * 100.0) / 100.0) + "MB";
    if (byteSize >= ONE_KB)
        return (Math.round(byteSize / ONE_KB * 100.0) / 100.0) + "KB";
    if (byteSize > 0.0) {
        return (Math.round(byteSize)) + "B";
    }
    return "0";
}

export function formatToByteSize(format: string): number {
    if (format.endsWith("EB")) {
        return parseFloat(format.substring(0, format.length - 2)) * ONE_EB;
    }
    if (format.endsWith("PB")) {
        return parseFloat(format.substring(0, format.length - 2)) * ONE_PB;
    }
    if (format.endsWith("TB")) {
        return parseFloat(format.substring(0, format.length - 2)) * ONE_TB;
    }
    if (format.endsWith("GB")) {
        return parseFloat(format.substring(0, format.length - 2)) * ONE_GB;
    }
    if (format.endsWith("MB")) {
        return parseFloat(format.substring(0, format.length - 2)) * ONE_MB;
    }
    if (format.endsWith("KB")) {
        return parseFloat(format.substring(0, format.length - 2)) * ONE_KB;
    }
    if (format.endsWith("B")) {
        return parseFloat(format.substring(0, format.length - 1));
    }
    return 0;
}
