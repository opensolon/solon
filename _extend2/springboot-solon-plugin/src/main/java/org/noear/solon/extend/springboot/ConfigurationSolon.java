package org.noear.solon.extend.springboot;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author noear
 * @since 1.2
 */
@Configuration
public class ConfigurationSolon  {
    @Bean
    public BeanPostProcessor beanPostProcessor(){
        return new BeanPostProcessorSolon();
    }

}
