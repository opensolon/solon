package com.swagger.demo;

import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtendSetting;
import com.github.xiaoymin.knife4j.solon.extension.OpenApiExtension;
import com.github.xiaoymin.knife4j.solon.extension.OpenApiSettingExtension;
import com.swagger.demo.model.HttpCodes;

import org.noear.solon.docs.ApiEnum;
import org.noear.solon.docs.ApiInfo;
import org.noear.solon.docs.DocDocket;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.Result;

@Configuration
public class Config {
    @Inject(value = "${knife4j.setting}",required = false)
    OpenApiExtendSetting setting = new OpenApiExtendSetting();

    /**
     * 基于配置构建
     */
    @Bean("adminApi")
    public DocDocket adminApi(@Inject("${swagger.adminApi}") DocDocket docket) {
        //docket.globalResult(SwaggerRes.class);
        docket.globalResponseCodes(new HttpCodes());
        docket.securityDefinitionInHeader("token");
        docket.vendorExtensions(new OpenApiExtension()
                .addProperty(new OpenApiSettingExtension(setting)));
        return docket;
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
                .apis("com.swagger.demo.controller.app")
                .securityDefinitionInHeader("token");

    }

    //    @Bean("appApi")
    public DocDocket appApi2() {
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
                .apis("com.swagger.demo.controller.app")
                .securityDefinitionInHeader("token");

    }
}
