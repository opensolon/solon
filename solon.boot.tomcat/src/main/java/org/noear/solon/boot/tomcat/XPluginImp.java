package org.noear.solon.boot.tomcat;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;

import java.io.Closeable;
import java.io.IOException;

public final class XPluginImp implements XPlugin, Closeable {
    private XPlugin _server = null;

    @Override
    public void start(XApp app) {
        long start_before = System.currentTimeMillis();
        String mode = XUtil.loadClass("org.apache.jasper.servlet.JspServlet") == null ? "pure" : "jsp";
        // pure 仅支持返回REST风格的JSON数据，渲染生产HTML页面需要render
        // jsp 支持使用jsp技术生成JSP Servlet生成HTMLs
        switch (mode) {
            case "pure":
                _server = new XPluginTomcat();
                break;
            case "jsp":
                _server = new XPluginTomcatJsp();
                break;
            default:
                _server = new XPluginTomcat();
                break; // 默认pure模式
        }
        //Tomcat运行需要调用阻塞方法
        _server.start(app);
        // _server.start(app);

        System.err.println("tomcat::start end @" + (System.currentTimeMillis() - start_before) + "ms");

    }

    @Override
    public void close() throws IOException {
        if(_server != null){
            ((Closeable)_server).close();
        }
    }
}
