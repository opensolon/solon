package demo;

import org.noear.solon.Solon;
import org.noear.solon.extend.dubbo3.EnableDubbo;

@EnableDubbo
public class App {

    public static void main(String[] args) {
        Solon.start(App.class, args);
    }

}
