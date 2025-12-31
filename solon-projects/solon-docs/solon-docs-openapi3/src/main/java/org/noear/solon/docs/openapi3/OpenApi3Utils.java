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
package org.noear.solon.docs.openapi3;

import io.swagger.v3.oas.models.OpenAPI;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiGroupResource;
import org.noear.solon.docs.util.BasicAuthUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Open Api v3 工具类
 *
 * @author noear
 * @since 2.4
 */
public class OpenApi3Utils {
    /**
     * 获取接口分组资源
     */
    public static String getApiGroupResourceJson() throws IOException {
        return JacksonSerializer.getInstance().serialize(getApiGroupResourceJson("/swagger/v3"));
    }

    public static String getSwaggerConfigJson() throws IOException {
        // 构建swaggerConfig
        List<ApiGroupResource> apiGroupResourceJson = getApiGroupResourceJson("/swagger/v3");

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("urls", apiGroupResourceJson);

        return JacksonSerializer.getInstance().serialize(stringObjectHashMap);
    }

    /**
     * 获取接口分组资源
     */
    public static List<ApiGroupResource> getApiGroupResourceJson(String resourceUri) throws IOException {
        List<BeanWrap> list = Objects.requireNonNull(Solon.context()).getWrapsOfType(DocDocket.class);

        return list.stream().filter(bw -> Utils.isNotEmpty(bw.name()))
                .map(bw -> {
                    DocDocket docDocket = bw.raw();

                    if (docDocket.isEnable()) {
                        String group = bw.name();
                        String groupName = docDocket.groupName();
                        String url = resourceUri + "?group=" + group;

                        if (docDocket.upstream() == null) {
                            return new ApiGroupResource(groupName, docDocket.version(), url, "");
                        } else {
                            return new ApiGroupResource(groupName, docDocket.version(), url, docDocket.upstream().getContextPath());
                        }
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


    }

    /**
     * 获取接口
     */
    public static String getApiJson(Context ctx, String group) throws IOException {
        DocDocket docket = Solon.context().getBean(group);

        if (docket == null) {
            return null;
        }

        if (!BasicAuthUtil.basicAuth(ctx, docket)) {
            BasicAuthUtil.response401(ctx);
            return null;
        }

        if (docket.globalResponseCodes().containsKey(200) == false) {
            docket.globalResponseCodes().put(200, "");
        }

        OpenAPI openAPI = new OpenApi3Builder(docket).build();

        if (docket.serializer() == null) {
            return JacksonSerializer.getInstance().serialize(openAPI);
        } else {
            return docket.serializer().serialize(openAPI);
        }
    }

    public static String getSwaggerJson(DocDocket docket) throws IOException {
        //本地模式
        OpenAPI openAPI = new OpenApi3Builder(docket).build();

        if (docket.serializer() == null) {
            return JacksonSerializer.getInstance().serialize(openAPI);
        } else {
            return docket.serializer().serialize(openAPI);
        }
    }
}