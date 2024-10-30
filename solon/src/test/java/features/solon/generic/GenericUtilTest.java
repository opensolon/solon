package features.solon.generic;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;

/**
 * @author noear 2024/10/30 created
 */
public class GenericUtilTest {
    @Test
    public void test() {
        Solon.start(GenericUtilTest.class, new String[0]);

        DemoService demoService = Solon.context().getBean(DemoService.class);
        boolean isOk = demoService.getBaseMapper() != null;

        System.out.println(isOk ? "ok" : "not ok");
    }
}
