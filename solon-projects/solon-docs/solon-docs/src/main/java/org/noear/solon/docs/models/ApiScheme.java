/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.docs.models;

/**
 * 接口协议架构
 *
 * @author noear
 * @since 2.2
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
