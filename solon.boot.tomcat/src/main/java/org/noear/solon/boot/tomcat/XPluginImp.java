package org.noear.solon.boot.tomcat;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XPlugin;

public final class XPluginImp implements XPlugin {
    private XPlugin _server = null;

    @Override
    public void start(XApp app) {
        long start_before = System.currentTimeMillis();
        //todo define a exclusive class for jsp configuration of Tomcat
        String mode = XUtil.loadClass("AAAKKK") == null ? "pure" : "jsp";
        // pure 仅支持返回REST风格的JSON数据，渲染生产HTML页面需要render
        // jsp 支持使用jsp技术生成JSP Servlet生成HTML
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
        new Thread(() -> _server.start(app)).start();
        // _server.start(app);

        System.err.println("tomcat::start end @" + (System.currentTimeMillis() - start_before) + "ms");

    }
}
