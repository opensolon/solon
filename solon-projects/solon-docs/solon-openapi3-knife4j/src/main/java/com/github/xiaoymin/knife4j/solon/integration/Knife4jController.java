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
package com.github.xiaoymin.knife4j.solon.integration;

import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.core.handle.Context;
import org.noear.solon.docs.openapi3.OpenApi3Utils;

import java.io.IOException;

/**
 * Swagger api Controller
 *
 * @author noear
 * @since 2.3
 */
public class Knife4jController {
    /**
     * swagger 获取分组信息
     */
    @Produces("application/json; charset=utf-8")
    @Mapping("swagger-resources")
    public String resources() throws IOException {
        return OpenApi3Utils.getApiGroupResourceJson();
    }

    // http://localhost:8081/v3/api-docs/swagger-config
    @Produces("application/json; charset=utf-8")
    @Mapping("v3/api-docs/swagger-config")
    @Get
    public String swaggerConfig() throws IOException {
        return OpenApi3Utils.getSwaggerConfigJson();
    }

    /**
     * swagger 获取分组接口数据
     */
    @Produces("application/json; charset=utf-8")
    @Mapping("swagger/v3")
    public String api(Context ctx, String group) throws IOException {
        return OpenApi3Utils.getApiJson(ctx, group);
    }
}