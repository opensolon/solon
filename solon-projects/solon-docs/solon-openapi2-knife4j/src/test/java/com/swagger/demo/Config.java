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
package com.swagger.demo;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtensionResolver;
import com.swagger.demo.model.HttpCodes;

import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;
import io.swagger.models.parameters.HeaderParameter;
import org.noear.solon.docs.ApiEnum;
import org.noear.solon.docs.models.ApiInfo;
import org.noear.solon.docs.DocDocket;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Result;

@Configuration
public class Config {
    @Inject
    OpenApiExtensionResolver openApiExtensionResolver;

    /**
     * 基于配置构建
     */
    @Bean("adminApi")
    public DocDocket adminApi(@Inject("${swagger.adminApi}") DocDocket docket) {
        //docket.globalResult(SwaggerRes.class);
        docket.globalResponseCodes(new HttpCodes());
        //docket.securityDefinitionInHeader("token");
        docket.basicAuth(openApiExtensionResolver.getSetting().getBasic());
        docket.securityExtensions("token", new ApiKeyAuthDefinition().name("Authorization").in(In.HEADER));
        docket.globalParams(new HeaderParameter().name("Authorization").required(true));
        docket.vendorExtensions(openApiExtensionResolver.buildExtensions());

        return docket;
    }

    /**
     * 基于代码构建
     */
    @Bean("restapi")
    public DocDocket restapi() {
        return new DocDocket()
                .groupName("restapi接口")
                .schemes(ApiEnum.SCHEMES_HTTP)
                .apis("com.swagger.demo.controller.restapi");
        //.securityDefinitionInHeader("token");

    }

    /**
     * 基于代码构建
     */
    @Bean("appApi")
    public DocDocket appApi() {
        return new DocDocket()
                .groupName("app端接口")
                .schemes(ApiEnum.SCHEMES_HTTP)
                .globalResult(Result.class)
                .globalResponseInData(true)
                .apis("com.swagger.demo.controller.app");
        //.securityDefinitionInHeader("token");

    }



    /**
     * 基于代码构建
     */
    @Bean("appApi2")
    public DocDocket appApi2() {
        return new DocDocket()
                .groupName("app2端接口")
                .schemes(ApiEnum.SCHEMES_HTTP)
                .apis("com.swagger.demo.controller.app2");
    }

    /**
     * 基于代码构建
     */
    @Bean("gatewayApi")
    public DocDocket gatewayApi() {
        return new DocDocket()
                .groupName("gateway端接口")
                .schemes(ApiEnum.SCHEMES_HTTP)
                .globalResult(Result.class)
                .globalResponseInData(true)
                //.securityExtensions("TOKEN", new ApiKeyAuthDefinition().in(In.HEADER))
                //.vendorExtensions("TOKEN", "xxx")
                .globalParams(new HeaderParameter().name("token").required(true))
                .apis("com.swagger.demo.controller.api2");
        //.securityDefinitionInHeader("token");

    }

    /**
     * 基于代码构建
     */
//    @Bean("removeApi")
    public DocDocket removeApi() {
        return new DocDocket()
                .groupName("removeApi端接口")
                .schemes(ApiEnum.SCHEMES_HTTP)
                .upstream("lb://user-service", "/user-service", "swagger/v2?group=removeApi");
        //.securityDefinitionInHeader("token");

    }

    //    @Bean("appApi")
    public DocDocket appApi_2() {
        return new DocDocket()
                .groupName("app端接口")
                .info(new ApiInfo().title("在线文档")
                        .description("在线API文档")
                        .termsOfService("https://gitee.com/noear/solon")
                        .contact("demo", "https://gitee.com/noear/solon", "demo@foxmail.com")
                        .version("1.0"))
                .schemes(ApiEnum.SCHEMES_HTTP)
                .globalResponseInData(true)
                .globalResult(Result.class)
                .apis("com.swagger.demo.controller.app");
        //.securityDefinitionInHeader("token");

    }
}