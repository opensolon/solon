package com.swagger.demo;


import org.noear.solon.Solon;
import org.noear.solon.docs.annotation.EnableSwagger2;

@EnableSwagger2
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }
}
