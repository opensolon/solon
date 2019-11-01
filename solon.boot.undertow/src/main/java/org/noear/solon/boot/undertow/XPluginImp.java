package org.noear.solon.boot.undertow;

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
        String mode = XUtil.loadClass("io.undertow.jsp.JspServletBuilder") == null ? "pure" : "jsp";
        // pure 仅支持返回REST风格的JSON数据，渲染生产HTML页面需要render
        // jsp 支持使用jsp技术生成JSP Servlet生成HTML
        switch (mode) {
            case "pure":
                _server = new XPluginUndertow();
                break;
            case "jsp":
                _server = new XPluginUndertowJsp();
                break;
            default:
                _server = new XPluginUndertow();
                break; // 默认pure模式
        }
        _server.start(app);
        System.err.println("undertow::start end @" + (System.currentTimeMillis() - start_before) + "ms");
    }

    @Override
    public void close() throws IOException {
        if(_server != null){
            ((Closeable)_server).close();
        }
    }
}
