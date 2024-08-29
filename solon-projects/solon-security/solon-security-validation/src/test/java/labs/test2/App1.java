package labs.test2;

import org.noear.solon.Solon;
import org.noear.solon.validation.ValidUtils;

/**
 * @author noear 2024/8/13 created
 */
public class App1 {
    public static void main(String[] args) {
        Solon.start(App1.class, args);

        Entity1 entity1 = new Entity1();
        ValidUtils.validateEntity(entity1);
    }
}
