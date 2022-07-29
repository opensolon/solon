package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.*;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.HttpSignalProps;
import org.noear.solon.boot.ssl.SslContextFactory;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.ext.NamedThreadFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public final class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    private HttpServer _server = null;

    public static String solon_boot_ver() {
        return "jdk http/" + Solon.cfg().version();
    }

    @Override
    public void start(AopContext context) {
        if (Solon.app().enableHttp() == false) {
            return;
        }

        //如果有jetty插件，就不启动了
        if (Utils.loadClass("org.noear.solon.boot.jetty.XPluginImp") != null) {
            return;
        }

        //如果有undrtow插件，就不启动了
        if (Utils.loadClass("org.noear.solon.boot.undertow.XPluginImp") != null) {
            return;
        }

        //如果有smarthttp插件，就不启动了
        if (Utils.loadClass("org.noear.solon.boot.smarthttp.XPluginImp") != null) {
            return;
        }

        context.beanOnloaded((ctx) -> {
            try {
                start0(Solon.app());
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        HttpSignalProps props = new HttpSignalProps();
        String _host = props.getHost();
        int _port = props.getPort();
        String _name = props.getName();

        long time_start = System.currentTimeMillis();

        PrintUtil.info("Server:main: Sun.net.HttpServer(jdkhttp)");


        if (System.getProperty(ServerConstants.SSL_KEYSTORE) != null) {
            // enable SSL if configured
            if (Utils.isNotEmpty(_host)) {
                _server = HttpsServer.create(new InetSocketAddress(_host, _port), 0);
            } else {
                _server = HttpsServer.create(new InetSocketAddress(_port), 0);
            }

            addSslConfig((HttpsServer) _server);
        } else {
            if (Utils.isNotEmpty(_host)) {
                _server = HttpServer.create(new InetSocketAddress(_host, _port), 0);
            } else {
                _server = HttpServer.create(new InetSocketAddress(_port), 0);
            }
        }

        HttpContext httpContext = _server.createContext("/", new JdkHttpContextHandler());
        httpContext.getFilters().add(new ParameterFilter());

        _server.setExecutor(Executors.newCachedThreadPool(new NamedThreadFactory("jdkhttp-")));
        _server.start();

        _signal = new SignalSim(_name, _port, "http", SignalType.HTTP);
        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        PrintUtil.info("Connector:main: jdkhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{http://localhost:" + _port + "}");
        PrintUtil.info("Server:main: jdkhttp: Started @" + (time_end - time_start) + "ms");
    }

    private void addSslConfig(HttpsServer httpsServer) throws IOException {
        SSLContext sslContext = SslContextFactory.create();

        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    // Initialise the SSL context
                    SSLContext c = SSLContext.getDefault();
                    SSLEngine engine = c.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // Get the default parameters
                    SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                    params.setSSLParameters(defaultSSLParameters);
                } catch (Throwable e) {
                    //"Failed to create HTTPS port"
                    EventBus.push(e);
                }
            }
        });
    }

    @Override
    public void stop() throws Throwable {
        if (_server == null) {
            return;
        }

        _server.stop(0);
        _server = null;
        PrintUtil.info("Server:main: jdkhttp: Has Stopped " + solon_boot_ver());
    }
}
