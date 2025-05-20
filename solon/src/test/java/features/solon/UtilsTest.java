package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.Utils;

/**
 * @author noear 2025/5/20 created
 */
public class UtilsTest {
    @Test
    public void asMap1() {
        assert Utils.asMap() != null;
        assert Utils.asMap().size() == 0;
    }

    @Test
    public void asMap2() {
        assert Utils.asMap("a", 1).size() == 1;
        assert Utils.asMap("a", 1, "b", 2).size() == 2;
        assert Utils.asMap("a", 1, "b", 2, "c", 3).size() == 3;
    }

    @Test
    public void asMap3() {
        Throwable err = null;
        try {
            Utils.asMap("a");
        } catch (Throwable ex) {
            err = ex;
        }

        assert err != null;
    }

    @Test
    public void asMap3_2() {
        Throwable err = null;
        try {
            Utils.asMap("a", 1, "b");
        } catch (Throwable ex) {
            err = ex;
        }

        assert err != null;
    }
}
