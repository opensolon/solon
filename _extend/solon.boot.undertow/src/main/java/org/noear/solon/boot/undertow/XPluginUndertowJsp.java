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
import org.noear.solon.boot.undertow.jsp.JspResourceManager;
import org.noear.solon.boot.undertow.jsp.JspTldLocator;
import org.noear.solon.core.XClassLoader;
import org.noear.solon.core.XPlugin;

import javax.servlet.MultipartConfigElement;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * @Created by: Yukai
 * @Date: 2019/3/28 15:50
 * @Description : Yukai is so handsome xxD
 */
public class XPluginUndertowJsp implements XPlugin {
    private static Undertow.Builder serverBuilder = null;
    private static Undertow _server = null;


    @Override
    public void start(XApp app) {
        try {
            setupJsp(app);
        } catch (Exception e) {
            e.printStackTrace();
        }
        _server.start();

    }


    public void setupJsp(XApp app) throws Exception {
        final ServletContainer container = ServletContainer.Factory.newInstance();
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
               .addServlet(JspServletBuilder.createServlet("JSPServlet", "*.jsp"))
               .addServlet(new ServletInfo("ACTServlet", UtHttpHandlerJsp.class).addMapping("/"));  //这个才是根据上下文对象`XContext`进行分发

        if (XServerProp.session_timeout > 0) {
            builder.setDefaultSessionTimeout(XServerProp.session_timeout);
        }

        HashMap<String, TagLibraryInfo> tagLibraryMap = JspTldLocator.createTldInfos("WEB-INF");

        JspServletBuilder.setupDeployment(builder, new HashMap<String, JspPropertyGroup>(), tagLibraryMap, new HackInstanceManager());

        DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();
        HttpHandler jsp_handler = manager.start();

        //************************** init server start******************
        serverBuilder = getInstance().setServerOption(UndertowOptions.ALWAYS_SET_KEEP_ALIVE, false);

        serverBuilder.addHttpListener(app.port(), "0.0.0.0");
        serverBuilder.setHandler(jsp_handler);

        _server = serverBuilder.build();

        //************************* init server end********************
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
