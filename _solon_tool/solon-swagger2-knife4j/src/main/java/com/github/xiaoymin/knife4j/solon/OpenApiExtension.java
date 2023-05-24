package com.github.xiaoymin.knife4j.solon;

import org.noear.solon.docs.ApiVendorExtension;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear
 * @since 2.2
 */
public class OpenApiExtension implements ApiVendorExtension {
    public static final String EXTENSION_NAME = "x-openapi";

    private final String name;
    private final Map<String, Object> value;

    public OpenApiExtension(){
        this(EXTENSION_NAME);
    }

    public OpenApiExtension(String name) {
        this.name = name;
        this.value = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getValue() {
        return value;
    }

    public OpenApiExtension addProperty(ApiVendorExtension vendorExtension) {
        value.put(vendorExtension.getName(), vendorExtension.getValue());

        return this;
    }
}

