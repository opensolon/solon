function isSimilar(a: any, b: any): boolean {
    if (a === b) {
        return true;
    }
    if (a == null || b == null) {
        return false;
    }
    if (typeof a !== typeof b) {
        return false;
    }
    if (typeof a === 'object') {
        if (Array.isArray(a) !== Array.isArray(b)) {
            return false;
        }
        if (Array.isArray(a)) {
            if (a.length !== b.length) {
                return false;
            }
            for (let i = 0; i < a.length; i++) {
                if (!isSimilar(a[i], b[i])) {
                    return false;
                }
            }
            return true;
        }
        const keysA = Object.keys(a);
        const keysB = Object.keys(b);
        if (keysA.length !== keysB.length) {
            return false;
        }
        for (const key of keysA) {
            if (!isSimilar(a[key], b[key])) {
                return false;
            }
        }
        return true;
    }
    return false;
}