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

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import org.noear.solon.Utils;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.ClassPathStaticRepository;

/**
 * @author noear
 * @since 2.2
 */
public class Knife4jPlugin implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        OpenApiExtensionResolver openApiExtensionResolver = context.wrapAndPut(OpenApiExtensionResolver.class).get();

        if (openApiExtensionResolver.getSetting().isEnable()) {
            String uiPath = "/";
            if (openApiExtensionResolver.getSetting().isProduction()) {
                if (context.app().cfg().isFilesMode() == false) {
                    //生产环境，只有 files 模式才能用（即开发模式）
                    StaticMappings.add(uiPath, new ClassPathStaticRepository("META-INF/resources"));
                }
            } else {
                //非生产环境，一直可用
                StaticMappings.add(uiPath, new ClassPathStaticRepository("META-INF/resources"));
            }

            //注册控制器
            context.app().add(uiPath, Knife4jController.class);

            //添加 auth
            context.subBeansOfType(DocDocket.class, bean -> {
                if (Utils.isEmpty(bean.basicAuth())) {
                    //如果没有定义，则用全局的配置
                    bean.basicAuth(openApiExtensionResolver.getSetting().getBasic());
                }

                bean.vendorExtensions(openApiExtensionResolver.buildExtensions());
            });
        }
    }
}
