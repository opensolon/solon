package org.noear.solon.boot.jetty;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.jetty.http.JtContainerInitializerProxy;
import org.noear.solon.boot.jetty.http.JtHttpContextHandler;
import org.noear.solon.boot.jetty.http.JtHttpContextServletHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class PluginJettyBase {
    /**
     * 创建连接器（支持https）
     *
     * @since 1.6
     * */
    protected ServerConnector getConnector(Server server) {
        //配置 //http://www.eclipse.org/jetty/documentation/jetty-9/index.html
        HttpConfiguration config = new HttpConfiguration();
        if (ServerProps.request_maxHeaderSize != 0) {
            config.setRequestHeaderSize(ServerProps.request_maxHeaderSize);
        }

        HttpConnectionFactory httpFactory = new HttpConnectionFactory(config);

        if (System.getProperty(ServerConstants.SSL_KEYSTORE) == null) {
            return new ServerConnector(server, httpFactory);
        } else {
            String sslKeyStore = System.getProperty(ServerConstants.SSL_KEYSTORE);
            String sslKeyStoreType = System.getProperty(ServerConstants.SSL_KEYSTORE_TYPE);
            String sslKeyStorePassword = System.getProperty(ServerConstants.SSL_KEYSTORE_PASSWORD);

            SslContextFactory.Server contextFactory = new SslContextFactory.Server();

            if (Utils.isNotEmpty(sslKeyStoreType)) {
                contextFactory.setKeyStoreType(sslKeyStoreType);
            }

            if (Utils.isNotEmpty(sslKeyStore)) {
                contextFactory.setKeyStorePath(sslKeyStore);
            }

            if (Utils.isNotEmpty(sslKeyStorePassword)) {
                contextFactory.setKeyStorePassword(sslKeyStorePassword);
            }

            SslConnectionFactory sslFactory = new SslConnectionFactory(contextFactory, HttpVersion.HTTP_1_1.asString());

            return new ServerConnector(server, sslFactory, httpFactory);
        }
    }

    protected ServletContextHandler getServletHandler() throws IOException {
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(JtHttpContextServletHandler.class, "/");
        handler.setBaseResource(new ResourceCollection(getResourceURLs()));


        //添加session state 支持
        if(Solon.global().enableSessionState()) {
            handler.setSessionHandler(new SessionHandler());

            if (ServerProps.session_timeout > 0) {
                handler.getSessionHandler().setMaxInactiveInterval(ServerProps.session_timeout);
            }
        }

        //添加容器初始器
        handler.addLifeCycleListener(new JtContainerInitializerProxy(handler.getServletContext()));

        return handler;
    }

    protected Handler getJettyHandler(){
        //::走Handler接口
        JtHttpContextHandler _handler = new JtHttpContextHandler();

        if(Solon.global().enableSessionState()) {
            //需要session state
            //
            SessionHandler s_handler = new SessionHandler();

            if (ServerProps.session_timeout > 0) {
                s_handler.setMaxInactiveInterval(ServerProps.session_timeout);
            }

            s_handler.setHandler(_handler);

            return s_handler;
        }else{
            //不需要session state
            //
            return _handler;
        }
    }


    protected String[] getResourceURLs() throws FileNotFoundException {
        URL rootURL = getRootPath();
        if (rootURL == null) {
            throw new FileNotFoundException("Unable to find root");
        }
        String resURL = rootURL.toString();

        boolean isDebug = Solon.cfg().isDebugMode();
        if (isDebug && (resURL.startsWith("jar:") == false)) {
            int endIndex = resURL.indexOf("target");
            String debugResURL = resURL.substring(0, endIndex) + "src/main/resources/";
            return new String[]{debugResURL, resURL};
        }

        return new String[]{resURL};
    }

    protected URL getRootPath() {
        URL root = Utils.getResource("/");
        if (root != null) {
            return root;
        }
        try {
            String path = Utils.getResource("").toString();
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
