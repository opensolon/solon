package org.noear.solon.extend.luffy.impl;

import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        JtRun.init();

        JtRun.xfunInit();
    }
}
