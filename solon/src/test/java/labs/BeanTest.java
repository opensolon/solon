package labs;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;

/**
 * @author noear 2023/7/23 created
 */
public class BeanTest {
    private final String a;

    BeanTest(String a) {
        this.a = a;
    }

    public static BeanTest create() {
        return new BeanTest("a");
    }

    public static void main(String[] args) {
        Solon.start(BeanTest.class, args, app -> {
            app.context().wrapAndPut(BeanTest.class, BeanTest.create());
        });
    }
}
