package labs.injectTest;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Managed;

/**
 * @author noear 2024/11/27 created
 */
@Managed
public class Case1 {
    @Inject("${demo.p1}")
    static int p1;

    public static void main(String[] args) {
        System.setProperty("demo.p1", "1");

        Solon.start(Case1.class, args);

        System.out.println(Case1.p1);
    }
}
