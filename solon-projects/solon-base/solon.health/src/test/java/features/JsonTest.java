package features;

import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2022/8/7 created
 */
public class JsonTest {
    @Test
    public void test(){
        Map<String,Object> data = new HashMap<>();
        data.put("c:\\","c:\\");

        System.out.println(ONode.stringify(data, options));
    }

    private static final Options options = Options.def()
            .add(Feature.EnumUsingName);

}
