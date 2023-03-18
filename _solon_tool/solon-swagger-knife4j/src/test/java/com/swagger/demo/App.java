package com.swagger.demo;


import org.noear.solon.Solon;
import org.noear.solon.swagger.annotation.EnableSwagger;

/**
 * @author lbq
 * @date 2020/06/16
 */
@EnableSwagger
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            app.get("/", c -> c.redirect("/swagger"));
        });
    }
}
