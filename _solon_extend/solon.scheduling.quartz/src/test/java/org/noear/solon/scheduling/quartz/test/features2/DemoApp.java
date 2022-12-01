package org.noear.solon.scheduling.quartz.test.features2;

import org.noear.solon.Solon;
import org.noear.solon.scheduling.annotation.EnableScheduling;

/**
 * @author noear 2022/12/1 created
 */
@EnableScheduling
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);
    }
}
