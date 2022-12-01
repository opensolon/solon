package org.noear.solon.scheduling.simple.test.features;

import org.noear.solon.Solon;
import org.noear.solon.scheduling.annotation.EnableScheduling;

/**
 * @author noear 2021/12/28 created
 */
@EnableScheduling
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);
    }
}
