package org.noear.solon.cloud.integration.springboot;

import org.noear.solon.cloud.annotation.CloudConfig;
import org.noear.solon.cloud.impl.CloudBeanInjector;
import org.noear.solon.extend.springboot.EnableSolon;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;

/**
 * @author noear
 * @since 1.2
 */
@EnableSolon
@Configuration
public class AutoConfigurationCloud extends InstantiationAwareBeanPostProcessorAdapter {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        //兼容1.0.x
        return null;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.startsWith("org.springframework") == false) {
            Class<?> beanClz = bean.getClass();

            ReflectionUtils.doWithFields(beanClz, (field -> {
                if (Modifier.isFinal(field.getModifiers())
                        || Modifier.isStatic(field.getModifiers())) {
                    return;
                }

                CloudConfig anno = field.getAnnotation(CloudConfig.class);

                if (anno != null) {
                    Object val = CloudBeanInjector.instance.build(field.getType(), anno);
                    field.setAccessible(true);
                    field.set(bean, val);
                }
            }));
        }

        return bean;
    }
}

