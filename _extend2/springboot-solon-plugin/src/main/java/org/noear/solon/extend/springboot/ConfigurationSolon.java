package org.noear.solon.extend.springboot;

import org.noear.solon.Solon;
import org.noear.solon.extend.servlet.SolonServletFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author noear 2020/12/28 created
 */
@Configuration
public class ConfigurationSolon implements ApplicationListener<ApplicationStartingEvent> {
    @Bean
    public FilterRegistrationBean servletRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new SolonServletFilter());

        return registration;
    }

    @Bean
    public BeanPostProcessor beanPostProcessor(){
        return new BeanPostProcessorSolon();
    }

    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        Solon.start(event.getSource().getClass(), event.getArgs());
    }
}
