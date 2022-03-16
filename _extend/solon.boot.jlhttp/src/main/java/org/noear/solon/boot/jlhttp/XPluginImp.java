package org.noear.solon.boot.jlhttp;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.boot.ServerConstants;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.ext.NamedThreadFactory;

import javax.net.ssl.SSLServerSocketFactory;
import java.util.concurrent.Executors;

//
// jlhttp: https://www.freeutils.net/source/jlhttp/
//

public final class XPluginImp implements Plugin {
    private static Signal _signal;
    public static Signal signal(){
        return _signal;
    }

    private HTTPServer _server = null;

    public static String solon_boot_ver(){
        return "jlhttp 2.6/" + Solon.cfg().version();
    }

    @Override
    public  void start(SolonApp app) {
        if(app.enableHttp() == false){
            return;
        }
        //如果有jetty插件，就不启动了
        if(Utils.loadClass("org.noear.solon.boot.jetty.XPluginImp") != null){
            return;
        }

        //如果有undrtow插件，就不启动了
        if(Utils.loadClass("org.noear.solon.boot.undertow.XPluginImp") != null){
            return;
        }

        Aop.beanOnloaded(() -> {
            start0(app);
        });
    }

    private void start0(SolonApp app) {
        _server = new HTTPServer();

        String _name = app.cfg().get(ServerConstants.SERVER_HTTP_NAME);
        int _port = app.cfg().getInt(ServerConstants.SERVER_HTTP_PORT, 0);
        if (_port < 1) {
            _port = app.port();
        }

        long time_start = System.currentTimeMillis();

        //maxHeaderSize def: 8k
        //maxFormContentSize def: 2m (from content)
        //maxBodySize def: -

        if(ServerProps.request_maxHeaderSize > 0) {
            HTTPServer.MAX_HEADER_SIZE = ServerProps.request_maxHeaderSize;
        }

        if(ServerProps.request_maxBodySize > 0) {
            HTTPServer.MAX_BODY_SIZE = ServerProps.request_maxBodySize;
        }


        JlHttpContextHandler _handler = new JlHttpContextHandler();


        if (System.getProperty(ServerConstants.SSL_KEYSTORE) != null) { // enable SSL if configured
            _server.setServerSocketFactory(SSLServerSocketFactory.getDefault());
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

        PrintUtil.info("Server:main: JlHttpServer 2.4(jlhttp)");

        try {
            _server.setExecutor(Executors.newCachedThreadPool(new NamedThreadFactory("jlhttp-")));
            _server.setPort(_port);
            _server.start();

            _signal= new SignalSim(_name, _port, "http", SignalType.HTTP);

            app.signalAdd(_signal);

            long time_end = System.currentTimeMillis();

            PrintUtil.info("Connector:main: jlhttp: Started ServerConnector@{HTTP/1.1,[http/1.1]}{http://localhost:" + _port + "}");
            PrintUtil.info("Server:main: jlhttp: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void stop() throws Throwable {
        if(_server != null) {
            _server.stop();
            _server = null;

            PrintUtil.info("Server:main: jlhttp: Has Stopped " + solon_boot_ver());
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
