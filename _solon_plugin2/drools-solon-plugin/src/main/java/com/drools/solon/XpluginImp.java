package com.drools.solon;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

import com.drools.solon.config.DroolsAutoConfiguration;

public class XpluginImp implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
    	context.lifecycle(-99, () -> {
    		context.beanMake(DroolsAutoConfiguration.class);
    	});
    }
}
