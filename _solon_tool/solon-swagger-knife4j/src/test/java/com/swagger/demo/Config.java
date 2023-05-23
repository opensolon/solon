package com.swagger.demo;

import com.swagger.demo.model.SwaggerHttpCode;
import com.swagger.demo.model.SwaggerRes;
import io.swagger.models.Contact;
import io.swagger.models.Scheme;
import io.swagger.solon.models.ApiInfo;
import io.swagger.solon.models.SwaggerDocket;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Result;

@Configuration
public class Config {
    /**
     * 基于配置构建
     */
    @Bean("adminApi")
    public SwaggerDocket adminApi(@Inject("${swagger.adminApi}") SwaggerDocket docket) {
        //docket.globalResult(SwaggerRes.class);
        docket.globalResponseCodes(new SwaggerHttpCode().getHttpCodes());
        docket.securityDefinitionInHeader("token");
        return docket;
    }

    /**
     * 基于代码构建
     */
    @Bean("appApi")
    public SwaggerDocket appApi() {
        return new SwaggerDocket()
                .groupName("app端接口")
                .schemes(Scheme.HTTP)
                .globalResult(Result.class)
                .globalResponseInData(true)
                .apis("com.swagger.demo.controller.app")
                .securityDefinitionInHeader("token");

    }

    //    @Bean("appApi")
    public SwaggerDocket appApi2() {
        return new SwaggerDocket()
                .groupName("app端接口")
                .info(new ApiInfo().title("在线文档")
                        .description("在线API文档")
                        .termsOfService("https://gitee.com/noear/solon")
                        .contact(new Contact().name("demo")
                                .url("https://gitee.com/noear/solon")
                                .email("demo@foxmail.com"))
                        .version("1.0"))
                .schemes(Scheme.HTTP)
                .globalResponseInData(true)
                .globalResult(SwaggerRes.class)
                .apis("com.swagger.demo.controller.app")
                .securityDefinitionInHeader("token");

    }
}
