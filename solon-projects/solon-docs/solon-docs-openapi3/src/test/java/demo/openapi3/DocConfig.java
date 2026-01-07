package demo.openapi3;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Bean;
import org.noear.solon.core.handle.Result;
import org.noear.solon.docs.DocDocket;
import org.noear.solon.docs.models.ApiContact;
import org.noear.solon.docs.models.ApiInfo;

@Configuration
public class DocConfig {
    /**
     * 简单点的
     */
    @Bean("appApi")
    public DocDocket appApi() {
        //根据情况增加 "knife4j.setting" （可选）
        return new DocDocket()
                .groupName("app端接口")
                .schemes("http")
                .apis("com.swagger.demo.controller.app");

    }

    /**
     * 丰富点的
     */
    @Bean("adminApi")
    public DocDocket adminApi() {
        //根据情况增加 "knife4j.setting" （可选）
        return new DocDocket()
                .groupName("admin端接口")
                .info(new ApiInfo().title("在线文档")
                        .description("在线API文档")
                        .termsOfService("https://gitee.com/noear/solon")
                        .contact(new ApiContact().name("demo")
                                .url("https://gitee.com/noear/solon")
                                .email("demo@foxmail.com"))
                        .version("1.0"))
                .schemes("http", "https")
                .globalResponseInData(true)
                .globalResult(Result.class)
                .apis("com.swagger.demo.controller.admin"); //可以加多条，以包名为单位

    }
}