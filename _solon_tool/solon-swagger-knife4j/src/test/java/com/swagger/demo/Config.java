package com.swagger.demo;

import com.swagger.demo.model.SwaggerRes;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.swagger.SwaggerConfig;

/**
 * @author noear 2022/4/13 created
 */
@Configuration
public class Config {
    @Bean
    public SwaggerConfig swaggerConfig() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        swaggerConfig.setCommonRet(SwaggerRes.class);

        return swaggerConfig;
    }
}
