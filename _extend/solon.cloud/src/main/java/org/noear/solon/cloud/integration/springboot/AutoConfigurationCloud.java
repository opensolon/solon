package org.noear.solon.cloud.integration.springboot;

import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.impl.CloudBeanInjector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

/**
 * @author noear
 * @since 1.2
 */
@Configuration
public class AutoConfigurationCloud extends InstantiationAwareBeanPostProcessorAdapter {

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

