package org.noear.solon.extend.springboot;

import org.noear.solon.Utils;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author noear 2020/12/29 created
 */
public class BeanPostProcessorSolon implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        BeanWrap bw = Aop.wrap(bean.getClass(), bean);

        Aop.context().beanRegister(bw, beanName, true);

        return bean;
    }
}
