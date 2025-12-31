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
package cn.ingrun.solon.knife4j.extension;

import org.noear.solon.docs.models.ApiVendorExtension;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 2.3
 */
public class OpenApiExtension implements ApiVendorExtension<Map> {
    public static final String EXTENSION_NAME = "x-openapi";

    private final String name;
    private final Map<String, Object> value;

    public OpenApiExtension() {
        this(EXTENSION_NAME);
    }

    public OpenApiExtension(String name) {
        this.name = name;
        this.value = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map getValue() {
        return value;
    }

    public OpenApiExtension addProperty(ApiVendorExtension vendorExtension) {
        value.put(vendorExtension.getName(), vendorExtension.getValue());

        return this;
    }
}

