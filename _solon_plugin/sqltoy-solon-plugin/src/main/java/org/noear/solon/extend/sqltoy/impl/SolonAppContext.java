package org.noear.solon.extend.sqltoy.impl;

import org.noear.solon.core.Aop;
import org.sagacity.sqltoy.integration.AppContext;

import java.util.HashMap;
import java.util.Map;

public class SolonAppContext implements AppContext {
    @Override
    public boolean containsBean(String s) {

        return Aop.has(s);
    }

    @Override
    public Object getBean(String s) {
        return Aop.get(s);
    }

    @Override
    public <T> T getBean(Class<T> aClass) {
        return Aop.get(aClass);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> aClass) {
        Map<String,T> beans = new HashMap<>();
        Aop.beanForeach(beanWrap -> {
            if(aClass.isInstance(beanWrap.get())){
                beans.put(beanWrap.name(),beanWrap.get());
            }
        });
        return beans;
    }
}
