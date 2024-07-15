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
package com.layjava.docs.javadoc.solon;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.layjava.docs.javadoc.solon.util.JsonUtil;
import io.swagger.models.Swagger;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Context;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiGroupResource;
import org.noear.solon.docs.util.BasicAuthUtil;

import java.io.IOException;
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

        return JsonUtil.toJson(resourceList);
    }

    /**
     * 获取接口
     */
    public static String getApiJson(Context ctx, String group) throws IOException {
        
        List<DocDocket> docDockets = Solon.context().getBeansOfType(DocDocket.class);

        if (CollUtil.isEmpty(docDockets)) {
            return "";
        }
        // 如果未指定分组，则取第一个分组
        if (StrUtil.isBlank(group)) {
            group = docDockets.get(0).groupName();
        }

        String finalGroup = group;
        // 找不到则取第一个
        DocDocket docket = docDockets.stream().filter(d -> d.groupName().equals(finalGroup)).findFirst().orElse(docDockets.get(0));

        if (!BasicAuthUtil.basicAuth(ctx, docket)) {
            BasicAuthUtil.response401(ctx);
            return null;
        }

        if (!docket.globalResponseCodes().containsKey(200)) {
            docket.globalResponseCodes().put(200, "");
        }

        Swagger swagger = new OpenApi2Builder(docket).build();
        return JsonUtil.toJson(swagger);
    }
}
