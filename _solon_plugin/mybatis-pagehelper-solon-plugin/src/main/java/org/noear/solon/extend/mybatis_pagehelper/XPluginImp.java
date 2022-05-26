package org.noear.solon.extend.mybatis_pagehelper;

import org.noear.solon.SolonApp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        context.beanMake(PageHelperConfiguration.class);
    }
}
