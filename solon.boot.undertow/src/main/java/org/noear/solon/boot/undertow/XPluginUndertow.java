package org.noear.solon.boot.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import org.noear.solon.XApp;
import org.noear.solon.XProperties;
import org.noear.solon.core.XPlugin;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

/**
 * @Created by: Yukai
 * @Date: 2019/3/28 15:49
 * @Description : Yukai is so handsome xxD
 */
public class XPluginUndertow implements XPlugin {
    // singleton
    private static Undertow.Builder serverBuilder = null;
    private Undertow _server = null;

    @Override
    public void start(XApp app) {
        Undertow.Builder builder = getInstance();
        builder.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE,false);

        // 动作分发Handler
        UtHttpExchangeHandler _handler = new UtHttpExchangeHandler(true, app);
        DeploymentManager manager = doGenerateManager(_handler, app);

        HttpHandler f_handler = null;
        try {
            f_handler = manager.start();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        builder.addHttpListener(app.port(), "0.0.0.0");
        builder.setHandler(f_handler);
        _server = builder.build();

        try {
            _server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.onStop(this::stop);
    }


    // 生成DeploymentManager来生成handler
    private DeploymentManager doGenerateManager(UtHttpExchangeHandler innerHandler, XApp app) {
        HandlerWrapper wrapper = nothing -> innerHandler;

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));
        DeploymentInfo builder = new DeploymentInfo()
                .setClassLoader(XPluginImp.class.getClassLoader())
                .setDeploymentName("YK")
                .setContextPath("/")
                .setDefaultMultipartConfig(multipartConfigElement)
                .addInnerHandlerChainWrapper(wrapper);

        XProperties props = app.prop();
        int s_timeout = props.getInt("server.session.timeout", 0);
        if (s_timeout > 0) {
            builder.setDefaultSessionTimeout(s_timeout);
        }

        ServletContainer _container = Servlets.defaultContainer();
        DeploymentManager deploymentManager = _container.addDeployment(builder);
        deploymentManager.deploy();
        return deploymentManager;

    }

    // 允许在其他代码层访问容器构造器实例
    public static Undertow.Builder getInstance() {
        synchronized (XPluginImp.class) {
            if (serverBuilder == null) {
                synchronized (XPlugin.class) {
                    serverBuilder = Undertow.builder();
                }
            }
        }
        return serverBuilder;
    }

    public void stop() {
        try {
            _server.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
