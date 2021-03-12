package org.noear.solon.extend.springboot;

import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author noear
 * @since 1.2
 */
public class BeanPostProcessorSolon implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //兼容1.0.x
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.startsWith("org.springframework") == false) {

            try {
                BeanWrap bw = Aop.wrap(bean.getClass(), bean);

                if (bean.getClass().getSimpleName().equalsIgnoreCase(beanName)) {
                    Aop.context().beanRegister(bw, null, true);
                } else {
                    Aop.context().beanRegister(bw, beanName, true);
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }

        return bean;
    }
}
