package com.github.xiaoymin.knife4j.solon.extension;

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

