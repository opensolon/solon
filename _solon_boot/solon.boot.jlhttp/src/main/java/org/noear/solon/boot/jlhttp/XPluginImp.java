package org.noear.solon.boot.jlhttp;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.prop.impl.HttpServerProps;
import org.noear.solon.boot.ssl.SslContextFactory;
import org.noear.solon.core.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.LogUtil;

//
// jlhttp: https://www.freeutils.net/source/jlhttp/
//

public final class XPluginImp implements Plugin {
    private static Signal _signal;

    public static Signal signal() {
        return _signal;
    }

    private HTTPServer _server = null;

    public static String solon_boot_ver() {
        return "jlhttp 2.6/" + Solon.cfg().version();
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
                throw new IllegalStateException(e);
            }
        });
    }

    private void start0(SolonApp app) throws Throwable {
        //初始化属性
        ServerProps.init();

        _server = new HTTPServer();

        HttpServerProps props = new HttpServerProps();
        String _host = props.getHost();
        int _port = props.getPort();
        String _name = props.getName();

        long time_start = System.currentTimeMillis();

        //maxHeaderSize def: 8k
        //maxFormContentSize def: 2m (from content)
        //maxBodySize def: -

        if (ServerProps.request_maxHeaderSize > 0) {
            HTTPServer.MAX_HEADER_SIZE = ServerProps.request_maxHeaderSize;
        }

        if (ServerProps.request_maxBodySize > 0) {
            HTTPServer.MAX_BODY_SIZE = ServerProps.request_maxBodySize;
        }

        JlHttpContextHandler _handler = new JlHttpContextHandler();


        if (System.getProperty(ServerConstants.SSL_KEYSTORE) != null) {
            // enable SSL if configured
            _server.setServerSocketFactory(SslContextFactory.create().getServerSocketFactory());
        }

        HTTPServer.VirtualHost host = _server.getVirtualHost(null);


        host.setDirectoryIndex(null);

        host.addContext("/", _handler,
                MethodType.HEAD.name,
                MethodType.GET.name,
                MethodType.POST.name,
                MethodType.PUT.name,
                MethodType.DELETE.name,
                MethodType.PATCH.name,
                MethodType.OPTIONS.name);

        LogUtil.global().info("Server:main: JlHttpServer 2.6(jlhttp)");
        
        _server.setExecutor(props.getBioExecutor("jlhttp-"));
        _server.setPort(_port);
        if (Utils.isNotEmpty(_host)) {
            _server.setHost(_host);
        }
        _server.start();

        _signal = new SignalSim(_name, _host, _port, "http", SignalType.HTTP);

        app.signalAdd(_signal);

        long time_end = System.currentTimeMillis();

        LogUtil.global().info("Connector:main: jlhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{http://localhost:" + _port + "}");
        LogUtil.global().info("Server:main: jlhttp: Started @" + (time_end - time_start) + "ms");
    }

    @Override
    public void stop() throws Throwable {
        if (_server != null) {
            _server.stop();
            _server = null;

            LogUtil.global().info("Server:main: jlhttp: Has Stopped " + solon_boot_ver());
        }
    }
}



/**
1389行修改：
添加：getOriginalUri()；解决getUri()，无法拿到域和端口问题

1366 + 2111行修改：
获取：socket 的址址，作为：remoteAddr（否则没有远程连接地址）

1491行修改：
添加_paramsList，实现参数寄存功能（流只能读一次，后面就没了）

2807行修改：
将编译改为：UTF-8；解决中文参数乱码问题

1748行修改：（优先使用传进来的contentType，解决内部404之类的调用无法显示为html的问题 ）
public void sendHeaders(int status, long length, long lastModified,
                                String etag, String contentType, long[] range) throws IOException {
String ct = headers.get("Content-Type");
            if (ct == null) {
                ct = contentType != null ? contentType : "application/octet-stream";
                headers.add("Content-Type", ct);
            }else {
                if (contentType != null) { //xyj,20181220
                    ct = contentType;
                    headers.replace("Content-Type", ct);
                }
            }


* */
