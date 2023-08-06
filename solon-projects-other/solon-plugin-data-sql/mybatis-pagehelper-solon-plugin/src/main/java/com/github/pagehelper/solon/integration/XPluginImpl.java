package com.github.pagehelper.solon.integration;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AopContext context) {
        context.beanMake(PageHelperConfiguration.class);
    }
}
