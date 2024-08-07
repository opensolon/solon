/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.docs.openapi2;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.List;
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
                    String group = bw.name();
                    String groupName = ((DocDocket) bw.raw()).groupName();
                    String url = resourceUri + "?group=" + group;

                    return new ApiGroupResource(groupName, "2.0", url);
                })
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
            Swagger swagger = new OpenApi2Builder(docket).build();

            if (docket.serializer() == null) {
                return JacksonSerializer.getInstance().serialize(swagger);
            } else {
                return docket.serializer().serialize(swagger);
            }
        } else {
            //代理模式
            String host = docket.upstream().getService();
            if (host.contains("://") == false) {
                host = LoadBalance.get(host).getServer();
            }

            String url = PathUtil.mergePath(host, docket.upstream().getPath()).substring(1);
            return httpGet(url);
        }
    }

    public static String httpGet(String urlStr) throws IOException {
        // 打开连接
        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();

        // 设置请求方法为GET
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

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
                return response.toString();
            }
        } else {
            throw new SocketException("HTTP GET failed: " + responseCode);
        }
    }
}