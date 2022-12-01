package org.noear.solon.scheduling.simple.test;

import org.noear.solon.Solon;
import org.noear.solon.scheduling.annotation.EnableScheduling;

/**
 * @author noear 2022/12/1 created
 */
@EnableScheduling
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }
}
