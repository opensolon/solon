package com.swagger.demo;


import org.noear.solon.Solon;
import io.swagger.solon.annotation.EnableSwagger;

@EnableSwagger
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            app.get("/", c -> c.redirect("/swagger"));
        });
    }
}
