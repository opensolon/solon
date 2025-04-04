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