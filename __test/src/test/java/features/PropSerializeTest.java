package features;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.codec.BeanEncoder;
import org.noear.solon.Solon;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 *
 * @author noear 2025/10/15 created
 *
 */
@SolonTest(App.class)
public class PropSerializeTest {
    @Test
    public void case1() {
        ONode oNode = BeanEncoder.encode(Solon.cfg());
        System.out.println(oNode.toJson());
    }
}
