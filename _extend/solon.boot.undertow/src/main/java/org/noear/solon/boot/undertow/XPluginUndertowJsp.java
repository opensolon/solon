package org.noear.solon.boot.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.*;
import io.undertow.servlet.util.DefaultClassIntrospector;
import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.boot.undertow.http.UtHttpHandlerJsp;
import org.noear.solon.boot.undertow.jsp.JspResourceManager;
import org.noear.solon.boot.undertow.jsp.JspServletEx;
import org.noear.solon.boot.undertow.jsp.JspTldLocator;
import org.noear.solon.boot.undertow.websocket.UtWsConnectionCallback;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XClassLoader;
import org.noear.solon.core.XPlugin;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContainerInitializer;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static io.undertow.Handlers.websocket;

/**
 * @author by: Yukai
 * @since: 2019/3/28 15:50
 */
public class XPluginUndertowJsp extends XPluginUndertowBase implements XPlugin {
    private static Undertow _server = null;

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
            builder.setHandler(httpHandler);
        }

        _server = builder.build();

        //************************* init server end********************
    }

    // 生成DeploymentManager来生成handler
    private DeploymentManager doGenerateManager() throws Exception{
        MultipartConfigElement configElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));



        DeploymentInfo builder = new DeploymentInfo()
                .setClassLoader(XPluginUndertowJsp.class.getClassLoader())
                .setDeploymentName("solon")
                .setContextPath("/")
                .setDefaultEncoding(XServerProp.encoding_request)
                .setDefaultMultipartConfig(configElement)
                .setClassIntrospecter(DefaultClassIntrospector.INSTANCE);

        //尝试添加容器初始器
        ServletContainerInitializer initializer = Aop.getOrNull(ServletContainerInitializer.class);
        if (initializer != null) {
            builder.addServletContainerInitializer(new ServletContainerInitializerInfo(initializer.getClass(), null));
        }

        builder.setEagerFilterInit(true);

        String fileRoot = getResourceRoot();
        builder.setResourceManager(new JspResourceManager(XClassLoader.global(), fileRoot))
                .addServlet(new ServletInfo("ACTServlet", UtHttpHandlerJsp.class).addMapping("/"))
                .addServlet(JspServletEx.createServlet("JSPServlet", "*.jsp"));

        if (XServerProp.session_timeout > 0) {
            builder.setDefaultSessionTimeout(XServerProp.session_timeout);
        }

        HashMap<String, TagLibraryInfo> tagLibraryMap = JspTldLocator.createTldInfos("WEB-INF");

        JspServletBuilder.setupDeployment(builder, new HashMap<String, JspPropertyGroup>(), tagLibraryMap, new HackInstanceManager());


        final ServletContainer container = ServletContainer.Factory.newInstance();

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
