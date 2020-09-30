package org.noear.solon.boot.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.util.DefaultClassIntrospector;
import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.boot.undertow.http.UtHttpHandlerJsp;
import org.noear.solon.boot.undertow.jsp.JspResourceManager;
import org.noear.solon.boot.undertow.jsp.JspServletEx;
import org.noear.solon.boot.undertow.jsp.JspTldLocator;
import org.noear.solon.core.XClassLoader;
import org.noear.solon.core.XPlugin;

import javax.servlet.MultipartConfigElement;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static io.undertow.Handlers.websocket;

/**
 * @author by: Yukai
 * @since: 2019/3/28 15:50
 */
public class XPluginUndertowJsp implements XPlugin {
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
        Undertow.Builder builder =  Undertow.builder();

        builder.setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);

        builder.addHttpListener(app.port(), "0.0.0.0");

        builder.setHandler(httpHandler);
//        builder.setHandler(websocket(new UtWebSocketHandler(), httpHandler));

        _server = builder.build();

        //************************* init server end********************
    }

    // 生成DeploymentManager来生成handler
    private DeploymentManager doGenerateManager() throws Exception{

        MultipartConfigElement configElement = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

        String fileRoot = getResourceRoot();

        DeploymentInfo builder = new DeploymentInfo()
                .setClassLoader(XPluginUndertowJsp.class.getClassLoader())
                .setDeploymentName("solon")
                .setContextPath("/")
                .setDefaultEncoding(XServerProp.encoding_request)
                .setDefaultMultipartConfig(configElement)
                .setClassIntrospecter(DefaultClassIntrospector.INSTANCE);


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

    private String getResourceRoot() throws FileNotFoundException {
        URL rootURL = getRootPath();
        if (rootURL == null) {
            throw new FileNotFoundException("Unable to find root");
        }
        String resURL = rootURL.toString();

        boolean isDebug = XApp.cfg().isDebugMode();
        if (isDebug && (resURL.startsWith("jar:") == false)) {
            int endIndex = resURL.indexOf("target");
            return resURL.substring(0, endIndex) + "src/main/resources/";
        }

        return "";
    }

    private URL getRootPath() {
        URL root = XUtil.getResource("/");
        if (root != null) {
            return root;
        }
        try {
            String path = XUtil.getResource("").toString();
            if (path.startsWith("jar:")) {
                int endIndex = path.indexOf("!");
                path = path.substring(0, endIndex + 1) + "/";
            } else {
                return null;
            }
            return new URL(path);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
