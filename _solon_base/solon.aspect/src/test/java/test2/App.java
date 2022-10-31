package test2;

import org.noear.solon.Solon;

/**
 * @author noear 2022/9/30 created
 */
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);

        Solon.context().getBean(TagServiceImpl.class).update((Tag)null);
    }
}
