package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.validation.ValidUtils;
import org.noear.solon.validation.annotation.Min;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.NotNull;
import org.noear.solon.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2025/2/8 created
 */
public class EntityTest2 {
    @Test
    public void case1() {
        DemoClz demo = new DemoClz();
        demo.list = new ArrayList<>();
        //demo.list.add("demo");
        DemoObj obj = new DemoObj();
        obj.str = "a";
        demo.list.add(obj);

        obj.list = new ArrayList<>();
        obj.list.add(new DemoItem());

        boolean isOk;
        try {
            ValidUtils.validateEntity(demo);
            isOk = false;
        } catch (Exception e) {
            isOk = e.getMessage().contains("@Min");
        }
        assert isOk;
    }

    @Test
    public void case2() {
        DemoClz demo = new DemoClz();
        demo.list = new ArrayList<>();
        //demo.list.add("demo");
        DemoObj obj = new DemoObj();
        obj.str = "a";
        demo.list.add(obj);

        DemoItem item = new DemoItem();
        item.val = 2;

        obj.list = new ArrayList<>();
        obj.list.add(item);

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

        @NotEmpty
        @Validated
        public List<DemoItem> list;
    }

    public static class DemoItem {
        @Min(1)
        public Integer val = 0;
    }
}