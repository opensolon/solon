package org.noear.solon.boot.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.boot.jetty.http.JtContainerInitializerProxy;
import org.noear.solon.boot.jetty.http.JtHttpContextHandler;
import org.noear.solon.boot.jetty.http.JtHttpContextServlet;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

import javax.servlet.ServletContainerInitializer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class XPluginJetty implements XPlugin {
    protected Server _server = null;

    @Override
    public void start(XApp app) {
        try {

            Class<?> wsClz = XUtil.loadClass("org.eclipse.jetty.websocket.server.WebSocketHandler");

            _server = new Server(app.port());

            //session 支持
            _server.setSessionIdManager(new DefaultSessionIdManager(_server));

            if(app.enableWebSocket() && wsClz != null){
                _server.setHandler(new HandlerHub(getServerHandler()));
            }else{
                //没有ws包 或 没有开启
                _server.setHandler(getServerHandler());
            }


            _server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize",
                    XServerProp.request_maxRequestSize);


            app.prop().forEach((k, v) -> {
                String key = k.toString();
                if (key.indexOf(".jetty.") > 0) {
                    _server.setAttribute(key, v);
                }
            });

            app.prop().onChange((k, v) -> {
                if (k.indexOf(".jetty.") > 0) {
                    _server.setAttribute(k, v);
                }
            });

            _server.start();
        } catch (Exception ex) {
            throw XUtil.throwableWrap(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;
        }
    }

    /**
     * 获取Server Handler
     * */
    protected Handler getServerHandler() throws IOException {
        if(XUtil.loadClass("org.eclipse.jetty.servlet.ServletContextHandler") == null){
            //::走Handler接口
            SessionHandler s_handler = new SessionHandler();

            if (XServerProp.session_timeout > 0) {
                s_handler.setMaxInactiveInterval(XServerProp.session_timeout);
            }

            JtHttpContextHandler _handler = new JtHttpContextHandler();
            s_handler.setHandler(_handler);

            return s_handler;
        }else{
            //::走Servlet接口（需要多个包）
            return getWebServerHandler();
        }
    }

    private Handler getWebServerHandler() throws IOException{
        ServletContextHandler handler = new ServletContextHandler();
        handler.setSessionHandler(new SessionHandler());
        handler.setContextPath("/");
        handler.addServlet(JtHttpContextServlet.class, "/");
        handler.setBaseResource(new ResourceCollection(getResourceURLs()));


        //添加容器初始器
        handler.addLifeCycleListener(new JtContainerInitializerProxy(handler.getServletContext()));


        if (XServerProp.session_timeout > 0) {
            handler.getSessionHandler().setMaxInactiveInterval(XServerProp.session_timeout);
        }


        return handler;
    }


    protected String[] getResourceURLs() throws FileNotFoundException {
        URL rootURL = getRootPath();
        if (rootURL == null) {
            throw new FileNotFoundException("Unable to find root");
        }
        String resURL = rootURL.toString();

        boolean isDebug = XApp.cfg().isDebugMode();
        if (isDebug && (resURL.startsWith("jar:") == false)) {
            int endIndex = resURL.indexOf("target");
            String debugResURL = resURL.substring(0, endIndex) + "src/main/resources/";
            return new String[]{debugResURL, resURL};
        }

        return new String[]{resURL};
    }

    protected URL getRootPath() {
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
