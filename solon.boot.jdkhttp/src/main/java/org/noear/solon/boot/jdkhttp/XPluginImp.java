package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.noear.solon.XApp;
import org.noear.solon.core.XPlugin;

import java.net.InetSocketAddress;

public final class XPluginImp implements XPlugin {
    private HttpServer _server = null;

    public static String solon_boot_ver(){
        return "jdk http jdk8/1.0.4.43";
    }

    @Override
    public  void start(XApp app) {
        if (app.enableHttp == false) {
            return;
        }

        XServerProp.init();


        long time_start = System.currentTimeMillis();

        JdkHttpContextHandler _handler = new JdkHttpContextHandler(app);

        System.out.println("solon.Server:main: Sun.net.HttpServer jdk8");

        try {
            _server = HttpServer.create(new InetSocketAddress(app.port()), 0);
            HttpContext context = _server.createContext("/", _handler);
            context.getFilters().add(new ParameterFilter());
            
            _server.start();

            long time_end = System.currentTimeMillis();

            System.out.println("solon.Connector:main: Started ServerConnector@{HTTP/1.1,[http/1.1]}{0.0.0.0:" + app.port() + "}");
            System.out.println("solon.Server:main: Started @" + (time_end - time_start) + "ms");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() throws Throwable {
        if(_server != null) {
            _server.stop(0);
            _server = null;

            System.out.println("solon.Server:main: Has Stopped " + solon_boot_ver());
        }
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
