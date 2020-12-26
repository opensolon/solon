package org.noear.solon.extend.consul.test;

import org.noear.nami.annotation.EnableNamiClient;
import org.noear.solon.Solon;

@EnableNamiClient
public class TestApplication {
    public static void main(String[] args) {
        Solon.start(TestApplication.class, args);
    }
}
