package org.noear.solon.extend.shiro;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.shiro.impl.EnvironmentLoaderListenerImpl;
import org.noear.solon.extend.shiro.impl.ShiroFilterImpl;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        Aop.wrapAndPut(EnvironmentLoaderListenerImpl.class);
        Aop.wrapAndPut(ShiroFilterImpl.class);
    }
}
