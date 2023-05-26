package org.noear.solon.docs.models;

/**
 * @author noear
 * @since 2.3
 */
public enum ApiScheme {
    HTTP("http"),
    HTTPS("https"),
    WS("ws"),
    WSS("wss");

    private final String value;

    ApiScheme(String value) {
        this.value = value;
    }

    public static ApiScheme forValue(String value) {
        ApiScheme[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            ApiScheme item = var1[var3];
            if (item.toValue().equalsIgnoreCase(value)) {
                return item;
            }
        }

        return null;
    }

    public String toValue() {
        return this.value;
    }
}
