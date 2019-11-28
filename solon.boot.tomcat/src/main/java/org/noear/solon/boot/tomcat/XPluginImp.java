package org.noear.solon.boot.tomcat;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;

import java.io.Closeable;
import java.io.IOException;

public final class XPluginImp implements XPlugin {
    private XPlugin _server = null;

    public static String solon_boot_ver(){
        return "tomcat 8.5.23/1.0.3-b1";
    }

    @Override
    public void start(XApp app) {
        XServerProp.init();

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
    public void stop() throws Throwable {
        if(_server != null){
           _server.stop();
            _server = null;

            System.out.println("solon.Server:main: Has Stopped " + solon_boot_ver());
        }
    }
}
