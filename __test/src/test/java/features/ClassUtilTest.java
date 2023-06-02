package features;

import org.junit.Test;
import org.noear.solon.core.util.ClassUtil;

/**
 * @author noear 2023/6/2 created
 */
public class ClassUtilTest {
    @Test
    public void hashClass() {
        assert ClassUtil.hasClass(() -> Claas1.class);
    }

    static class Claas1 {
        static {
           a();
        }

        static void a(){
            throw new RuntimeException("");
        }
    }
}
