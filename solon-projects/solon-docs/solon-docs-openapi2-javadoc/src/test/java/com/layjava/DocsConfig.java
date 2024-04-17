package com.layjava;

import io.swagger.models.Scheme;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.docs.DocDocket;

@Configuration
public class DocsConfig {

    /**
     * 简单点的
     */
    @Bean("appApi")
    public DocDocket appApi() {
        return new DocDocket()
                .groupName("app端接口")
                .schemes(Scheme.HTTP.toValue())
                .apis("com.layjava.test");

    }

}
