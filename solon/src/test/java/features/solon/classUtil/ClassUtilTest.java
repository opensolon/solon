package features.solon.classUtil;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.exception.ConstructionException;
import org.noear.solon.core.util.ClassUtil;

import java.lang.reflect.Constructor;

/**
 * @author noear 2024/12/13 created
 */
public class ClassUtilTest {
    @Test
    public void case1() {
        try {
            ClassUtil.newInstance(Demo.class);
        } catch (ConstructionException e) {
            e.printStackTrace();
            assert e.getMessage().contains("$Demo");
        }
    }

    @Test
    public void case2() throws Exception {
        try {
            Constructor constructor = Demo.class.getConstructor();
            ClassUtil.newInstance(constructor, new Object[0]);
        } catch (ConstructionException e) {
            e.printStackTrace();
            assert e.getMessage().contains("$Demo");
        }
    }

    public static class Demo {
        public Demo() {
            throw new IllegalStateException("ddd");
        }
    }
}
