package com.dtflys.forest.config;

import org.noear.solon.core.AopContext;

/**
 * @author noear
 * @since 1.11
 */
public class SolonForestProperties extends ForestProperties {
    final AopContext context;
    public SolonForestProperties(AopContext context){
        this.context = context;
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        return context.cfg().getProperty(name, defaultValue);
    }
}
