package org.noear.solon.extend.consul.test;

import org.noear.nami.annotation.EnableNamiClient;
import org.noear.solon.Solon;
import org.noear.solon.extend.consul.ConsulKvUpdateEvent;

@EnableNamiClient
public class TestApplication {
    public static void main(String[] args) {
        Solon.start(TestApplication.class, args,app->{
            app.onEvent(ConsulKvUpdateEvent.class,(e)->{
                System.out.println(e.getValues());
            });
        });
    }
}
