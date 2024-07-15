/*
 * Copyright 2017-2024 noear.org and authors
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
        EventBus.publish(signal);


        //添加过滤器注册
        FilterRegistrationBean registration = new FilterRegistrationBean<>();
        registration.setFilter(new SolonServletFilter());
        return registration;
    }
}
