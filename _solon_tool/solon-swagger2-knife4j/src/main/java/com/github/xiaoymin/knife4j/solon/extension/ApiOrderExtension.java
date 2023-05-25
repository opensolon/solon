package com.github.xiaoymin.knife4j.solon.extension;

import org.noear.solon.docs.models.ApiVendorExtension;

/**
 * @author noear
 * @since 2.3
 */
public class ApiOrderExtension implements ApiVendorExtension<Integer> {
    private final Integer order;

    public ApiOrderExtension(Integer order) {
        this.order = order;
    }

    public String getName() {
        return "x-order";
    }

    public Integer getValue() {
        return this.order;
    }
}