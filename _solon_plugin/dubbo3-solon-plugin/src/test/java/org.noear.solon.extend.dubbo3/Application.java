package org.noear.solon.extend.dubbo3;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.noear.solon.Solon;

@EnableDubbo
public class Application {

    public static void main(String[] args) {
        Solon.start(Application.class, args);
    }

}
