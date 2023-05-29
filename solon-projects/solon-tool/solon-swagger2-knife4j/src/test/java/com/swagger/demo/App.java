package com.swagger.demo;


import org.noear.solon.Solon;

public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            //开发模式，或调试模式下才开启文档（或者自己定义配置控制）//或者一直开着
            app.enableDoc(app.cfg().isDebugMode() || app.cfg().isFilesMode());
        });
    }
}
