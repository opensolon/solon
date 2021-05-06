package org.noear.solon.extend.shiro.impl;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.EnvironmentLoader;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.noear.solon.core.Aop;
import org.noear.solon.core.event.EventBus;

import javax.servlet.ServletContextEvent;

/**
 * @author noear 2021/5/6 created
 */
public class EnvironmentLoaderListenerImpl extends EnvironmentLoaderListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);

        SecurityManagerImpl securityManager = new SecurityManagerImpl();

        //通过事件进行扩展
        EventBus.push(securityManager);

        sce.getServletContext().setAttribute(EnvironmentLoader.ENVIRONMENT_ATTRIBUTE_KEY, securityManager);

        Aop.getAsyn(Realm.class, (bw) -> {
            securityManager.setRealm(bw.raw());
        });
    }
}
