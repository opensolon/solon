package org.noear.solon.boot.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import io.undertow.servlet.util.DefaultClassIntrospector;
import org.noear.solon.XApp;
import org.noear.solon.boot.undertow.http.UtHttpHandler;
import org.noear.solon.boot.undertow.websocket.UtWsConnectionCallback;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContainerInitializer;

import static io.undertow.Handlers.websocket;

/**
 * @author  by: Yukai
 * @since : 2019/3/28 15:49
 */
public class XPluginUndertow extends XPluginUndertowBase implements XPlugin {
    private Undertow _server = null;

    @Override
    public void start(XApp app) {
        try {
            setup(app);

            _server.start();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void setup(XApp app) throws Throwable {
        // 动作分发Handler
        DeploymentManager manager = doGenerateManager();
        HttpHandler httpHandler = manager.start();


        //************************** init server start******************
        Undertow.Builder builder = Undertow.builder();

        builder.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);

        builder.addHttpListener(app.port(), "0.0.0.0");

        if (app.enableWebSocket()) {
            builder.setHandler(websocket(new UtWsConnectionCallback(), httpHandler));
        } else {
            //没有ws包 或 没有开启
            builder.setHandler(httpHandler);
        }

        _server = builder.build();

        //************************* init server end********************
    }


    // 生成DeploymentManager来生成handler
    private DeploymentManager doGenerateManager() throws Exception {

        UtHttpHandler handler = new UtHttpHandler();

        MultipartConfigElement configElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

        DeploymentInfo builder = new DeploymentInfo()
                .setClassLoader(XPluginImp.class.getClassLoader())
                .setDeploymentName("solon")
                .setContextPath("/")
                .setDefaultEncoding(XServerProp.encoding_request)
                .setDefaultMultipartConfig(configElement)
                .setClassIntrospecter(DefaultClassIntrospector.INSTANCE);

        //尝试添加容器初始器
        Aop.beanForeach((k, bw)-> {
            if (bw.raw() instanceof ServletContainerInitializer) {
                ServletContainerInitializer initializer = bw.raw();
                if (initializer != null) {
                    builder.addServletContainerInitializer(new ServletContainerInitializerInfo(initializer.getClass(), null));
                }
            }
        });



        builder.setEagerFilterInit(true);

        builder.addInnerHandlerChainWrapper(h -> handler);

        if (XServerProp.session_timeout > 0) {
            builder.setDefaultSessionTimeout(XServerProp.session_timeout);
        }

        final ServletContainer container = Servlets.defaultContainer();

        DeploymentManager deploymentManager = container.addDeployment(builder);
        deploymentManager.deploy();

        return deploymentManager;

    }


    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }
}
