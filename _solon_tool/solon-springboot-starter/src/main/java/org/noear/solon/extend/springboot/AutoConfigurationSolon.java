package org.noear.solon.extend.springboot;

import org.noear.solon.Solon;
import org.noear.solon.core.Signal;
import org.noear.solon.core.SignalSim;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.web.servlet.SolonServletFilter;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author noear
 * @since 1.2
 */
@Configuration
public class AutoConfigurationSolon {
    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new BeanPostProcessorSolon();
    }

    @Bean
    @ConditionalOnWebApplication
    public FilterRegistrationBean servletRegistrationBean() {
        //如果是WEB，注册一个信号
        Signal signal = new SignalSim(Solon.cfg().appName(), Solon.cfg().serverHost(), Solon.cfg().serverPort(), "http", SignalType.HTTP);
        Solon.app().signalAdd(signal);
        EventBus.push(signal);


        //添加过滤器注册
        FilterRegistrationBean registration = new FilterRegistrationBean<>();
        registration.setFilter(new SolonServletFilter());
        return registration;
    }
}
