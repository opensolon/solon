package org.noear.solon.extend.springboot;

import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.event.EventBus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author noear
 * @since 1.2
 */
public class BeanPostProcessorSolon implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.startsWith("org.springframework") == false) {
            if (bean.getClass().getAnnotations().length > 0) {
                try {
                    //
                    //支持Solon的注入能力（别的 solon 注入注解就不需要另外适配了）
                    //
                    Aop.context().beanInject(bean);
                } catch (Throwable ex) {
                    EventBus.push(ex);
                }
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.startsWith("org.springframework") == false) {

            if (bean.getClass().getAnnotations().length > 0) {
                try {
                    //
                    //同步到Solon的容器
                    //
                    BeanWrap bw = Aop.wrap(bean.getClass(), bean);

                    if (bean.getClass().getSimpleName().equalsIgnoreCase(beanName)) {
                        Aop.context().beanRegister(bw, null, true);
                    } else {
                        Aop.context().beanRegister(bw, beanName, true);
                    }
                } catch (Throwable ex) {
                    EventBus.push(ex);
                }
            }
        }

        return bean;
    }
}
