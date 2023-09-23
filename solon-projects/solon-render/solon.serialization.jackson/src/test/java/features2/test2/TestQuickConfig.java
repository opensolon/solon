package features2.test2;

import features2.model.OrderDo;
import features2.model.UserDo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.jackson.JacksonRenderFactory;
import org.noear.solon.test.SolonJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2023/1/16 created
 */
@Import(propertySource = "classpath:features2_test2.yml")
@RunWith(SolonJUnit4ClassRunner.class)
public class TestQuickConfig {
    @Inject
    JacksonRenderFactory renderFactory;

    @Test
    public void hello2() throws Throwable{
        UserDo userDo = new UserDo();

        Map<String, Object> data = new HashMap<>();
        data.put("time", new Date(1673861993477L));
        data.put("long", 12L);
        data.put("int", 12);
        data.put("null", null);

        userDo.setMap1(data);

        ContextEmpty ctx = new ContextEmpty();
        renderFactory.create().render(userDo, ctx);
        String output = ctx.attr("output");

        System.out.println(output);

        //完美
        assert "{\"b1\":true,\"d1\":1.0,\"map1\":{\"time\":\"2023-01-16 17:39:53\",\"long\":\"12\",\"int\":12},\"n1\":\"1\",\"s1\":\"noear\"}".equals(output);
    }

    @Test
    public void hello3() throws Throwable{
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("long", 1l);
        data.put("order", new OrderDo());

        ContextEmpty ctx = new ContextEmpty();
        renderFactory.create().render(data, ctx);
        String output = ctx.attr("output");

        System.out.println(output);

        assert "{\"long\":\"1\",\"order\":{\"orderId\":\"2\"}}".equals(output);
    }
}
