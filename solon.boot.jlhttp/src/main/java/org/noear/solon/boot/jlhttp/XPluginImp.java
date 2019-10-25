package org.noear.solon.boot.jlhttp;

import org.noear.solon.XApp;
import org.noear.solon.core.XMethod;
import org.noear.solon.core.XPlugin;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class XPluginImp implements XPlugin {
    private HTTPServer _server = new HTTPServer();

    @Override
    public  void start(XApp app) {
        long time_start = System.currentTimeMillis();

        JlHttpContextHandler _handler = new JlHttpContextHandler( app);

        _server.setPort(app.port());
        _server.setExecutor(new ThreadPoolExecutor(
                8,
                200,
                60000L, //1分钟
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>()));

        HTTPServer.VirtualHost host = _server.getVirtualHost((String) null);

        host.setDirectoryIndex(null);

        host.addContext("/", _handler,
                XMethod.GET.name,
                XMethod.POST.name,
                XMethod.PUT.name,
                XMethod.DELETE.name,
                XMethod.PATCH.name);

        System.out.println("oejs.Server:main: JlHttpServer 2.4");

        try {
            _server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("oejs.AbstractConnector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("oejs.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        app.onStop(this::stop);
    }

    public void stop() {
        _server.stop();
    }
}



/*
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
