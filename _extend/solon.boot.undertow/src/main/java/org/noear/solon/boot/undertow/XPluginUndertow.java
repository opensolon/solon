package org.noear.solon.boot.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.util.DefaultClassIntrospector;
import org.noear.solon.XApp;
import org.noear.solon.boot.undertow.http.UtHttpHandler;
import org.noear.solon.boot.undertow.jsp.JspResourceManager;
import org.noear.solon.boot.undertow.websocket.UtWsConnectionCallback;
import org.noear.solon.core.XClassLoader;
import org.noear.solon.core.XPlugin;

import javax.servlet.MultipartConfigElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

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
    private DeploymentManager doGenerateManager() throws Exception{
        UtHttpHandler handler = new UtHttpHandler();

        MultipartConfigElement configElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

        DeploymentInfo builder = new DeploymentInfo()
                .setClassLoader(XPluginImp.class.getClassLoader())
                .setDeploymentName("solon")
                .setContextPath("/")
                .setDefaultEncoding(XServerProp.encoding_request)
                .setDefaultMultipartConfig(configElement)
                .setClassIntrospecter(DefaultClassIntrospector.INSTANCE);

        builder.addInnerHandlerChainWrapper(h -> handler);

        builder.setEagerFilterInit(true);

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
