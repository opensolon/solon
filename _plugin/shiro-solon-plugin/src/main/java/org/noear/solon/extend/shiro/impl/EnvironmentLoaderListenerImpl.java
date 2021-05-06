package org.noear.solon.extend.shiro.impl;

import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.web.env.EnvironmentLoader;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.noear.solon.core.Aop;

import javax.servlet.ServletContextEvent;

/**
 * @author noear
 * @since 1.3
 */
public class EnvironmentLoaderListenerImpl extends EnvironmentLoaderListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);

        Aop.getAsyn(SessionsSecurityManager.class, (bw) -> {
            sce.getServletContext().setAttribute(EnvironmentLoader.ENVIRONMENT_ATTRIBUTE_KEY, bw.raw());
        });
    }
}
