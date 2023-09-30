package org.noear.solon.extend.sqltoy.impl;

import org.noear.solon.core.AppContext;

import java.util.HashMap;
import java.util.Map;

public class SolonAppContext implements org.sagacity.sqltoy.integration.AppContext {
    AppContext context;

    public SolonAppContext(AppContext context) {
        this.context = context;
    }

    @Override
    public boolean containsBean(String s) {
        return context.hasWrap(s);
    }

    @Override
    public Object getBean(String s) {
        return context.getBean(s);
    }

    @Override
    public <T> T getBean(Class<T> aClass) {
        return context.getBean(aClass);
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> aClass) {
        Map<String, T> beans = new HashMap<>();

        context.beanForeach(beanWrap -> {
            if (aClass.isInstance(beanWrap.get())) {
                beans.put(beanWrap.name(), beanWrap.get());
            }
        });
        return beans;
    }
}
