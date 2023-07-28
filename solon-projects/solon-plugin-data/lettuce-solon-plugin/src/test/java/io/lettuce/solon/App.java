package io.lettuce.solon;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.SolonMain;

@SolonMain
public class App {
    public static void main(String[] args) {
        SolonApp start = Solon.start(App.class, args);
        System.out.println("Greetings from Solon Lettuce!");
        DemoService demoService = start.context().getBean(DemoService.class);
        demoService.demoSet();
    }
}
