package org.noear.nami.springboot;

import org.noear.nami.Nami;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 动态生成nami的client
 * @param <T>
 */
public class NamiClientFactory<T> implements FactoryBean<T> {

    @Autowired
    NamiConfigurationSpring config;
    private Class<T> interfaceClass;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public T getObject() throws Exception {
        return Nami.builder().create(interfaceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }
}
