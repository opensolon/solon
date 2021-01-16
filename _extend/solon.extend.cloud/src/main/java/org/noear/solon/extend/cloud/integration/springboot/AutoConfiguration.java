package org.noear.solon.extend.cloud.integration.springboot;

import org.noear.solon.extend.cloud.annotation.CloudConfig;
import org.noear.solon.extend.cloud.impl.CloudBeanInjector;
import org.noear.solon.extend.springboot.SpringBootLinkSolon;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

/**
 * @author noear 2021/1/16 created
 */
@SpringBootLinkSolon
@Configuration
public class AutoConfiguration extends InstantiationAwareBeanPostProcessorAdapter {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClz = bean.getClass();

        ReflectionUtils.doWithFields(beanClz, (field -> {
            CloudConfig anno = field.getAnnotation(CloudConfig.class);

            if (anno != null) {
                Object val = CloudBeanInjector.instance.build(field.getType(), anno);
                field.setAccessible(true);
                field.set(bean, val);
            }
        }));

        return bean;
    }
}

