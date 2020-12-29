package org.noear.solon.extend.springboot;

import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author noear 2020/12/29 created
 */
public class BeanPostProcessorSolon implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        BeanWrap bw = Aop.wrap(bean.getClass(), bean);

        if (Utils.isEmpty(beanName)) {
            Aop.context().putWrap(bean.getClass(), bw);
        } else {
            Aop.context().putWrap(beanName, bw);
        }

        return bean;
    }
}
