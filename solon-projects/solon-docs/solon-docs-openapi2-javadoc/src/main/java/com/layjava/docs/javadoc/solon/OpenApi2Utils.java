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
package com.layjava.docs.javadoc.solon;

import io.swagger.models.Swagger;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.LoadBalance;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiGroupResource;
import org.noear.solon.docs.util.BasicAuthUtil;
import org.noear.solon.exception.SolonException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Open Api v2 工具类
 *
 * @author noear
 * @since 2.4
 */
public class OpenApi2Utils {
    /**
     * 获取接口分组资源
     */
    public static String getApiGroupResourceJson() throws IOException {
        return getApiGroupResourceJson("/swagger/v2");
    }

    /**
     * 获取接口分组资源
     */
    public static String getApiGroupResourceJson(String resourceUri) throws IOException {
        List<BeanWrap> list = Solon.context().getWrapsOfType(DocDocket.class);

        List<ApiGroupResource> resourceList = list.stream().filter(bw -> Utils.isNotEmpty(bw.name()))
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
                .filter(r -> r != null)
                .collect(Collectors.toList());

        return JacksonSerializer.getInstance().serialize(resourceList);
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

        if (docket.upstream() == null) {
            //本地模式
            return getSwaggerJson(docket, null);
        } else {
            //代理模式
            URI upstreamTarget = docket.upstream().getTarget();
            String targetAddr = LoadBalance.parse(upstreamTarget).getServer();
            if (targetAddr == null) {
                throw new SolonException("The target service does not exist (" + upstreamTarget + ")");
            }

            String url = PathUtil.joinUri(targetAddr, docket.upstream().getUri());
            return httpGet(url, docket);
        }
    }

    /**
     * @since 2.9
     */
    public static String getSwaggerJson(DocDocket docket, String description) throws IOException {
        //本地模式
        Swagger swagger = new OpenApi2Builder(docket).build(description);

        if (docket.serializer() == null) {
            return JacksonSerializer.getInstance().serialize(swagger);
        } else {
            return docket.serializer().serialize(swagger);
        }
    }

    /**
     * @since 2.9
     */
    private static String httpGet(String urlStr, DocDocket docket) throws IOException {
        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();

        if (Utils.isNotEmpty(docket.basicAuth())) {
            for (Map.Entry<String, String> kv : docket.basicAuth().entrySet()) {
                String auth = BasicAuthUtil.base64EncodeToStr(kv.getKey(), kv.getValue());
                connection.setRequestProperty("Authorization", "Basic " + auth);
                break;
            }
        }
        // 设置请求方法为GET
        int responseCode;
        try {
            connection.setRequestMethod("GET");
            responseCode = connection.getResponseCode();
        } catch (Exception e) {
            return getSwaggerJson(docket, "ERROR: HTTP connection failed: " + urlStr);
        }

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 接收响应
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                // 读取响应数据
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // 打印结果
                String apiJson = response.toString();

                if (Utils.isEmpty(apiJson)) {
                    return getSwaggerJson(docket, "ERROR: HTTP GET blank content: " + urlStr);
                } else {
                    return apiJson;
                }
            }
        } else {
            return getSwaggerJson(docket, "ERROR: HTTP GET failed: " + responseCode + ", " + urlStr);
            //throw new SocketException("HTTP GET failed: " + responseCode + ", " + urlStr);
        }
    }
}
