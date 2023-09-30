package com.dtflys.forest.config;

import org.noear.solon.core.AppContext;

/**
 * @author noear
 * @since 1.11
 */
public class SolonForestProperties extends ForestProperties {
    final AppContext context;
    public SolonForestProperties(AppContext context){
        this.context = context;
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        return context.cfg().getProperty(name, defaultValue);
    }
}
