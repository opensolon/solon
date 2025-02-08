package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.validation.ValidUtils;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2025/2/8 created
 */
public class EntityTest {
    @Test
    public void case1() {
        boolean isOk;
        DemoClz demo = new DemoClz();

        try {
            ValidUtils.validateEntity(demo);
            isOk = false;
        } catch (Exception e) {
            e.printStackTrace();
            isOk = e.getMessage().contains("@NotEmpty");
        }

        assert isOk;
    }

    @Test
    public void case2() {
        boolean isOk;
        DemoClz demo = new DemoClz();
        demo.list = new ArrayList<>();
        demo.list.add("demo");
        demo.list.add(new DemoObj());

        try {
            ValidUtils.validateEntity(demo);
            isOk = false;
        } catch (Exception e) {
            isOk = e.getMessage().contains("@NotNull");
        }

        assert isOk;
    }

    @Test
    public void case3() {
        DemoClz demo = new DemoClz();
        demo.list = new ArrayList<>();
        //demo.list.add("demo");
        DemoObj obj = new DemoObj();
        obj.str = "a";
        demo.list.add(obj);

        ValidUtils.validateEntity(demo);
        assert true;
    }

    public static class DemoClz {
        @NotEmpty
        @Validated
        public List<Object> list;
    }

    public static class DemoObj {
        @NotNull
        public String str;
    }
}
