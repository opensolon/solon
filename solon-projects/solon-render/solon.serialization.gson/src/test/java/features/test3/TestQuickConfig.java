package features.test3;

import features.model.UserDo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.gson.GsonRenderFactory;
import org.noear.solon.test.SolonJUnit5Extension;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2023/1/16 created
 */
@Import(propertySource = "classpath:features2_test3.yml")
@ExtendWith(SolonJUnit5Extension.class)
public class TestQuickConfig {
    @Inject
    GsonRenderFactory renderFactory;

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

        //error: int 没转为 string
        assert "{\"s0\":\"\",\"s1\":\"noear\",\"b0\":0,\"b1\":1,\"n0\":\"0\",\"n1\":\"1\",\"d0\":0,\"d1\":1.0,\"obj0\":null,\"list0\":null,\"map0\":null,\"map1\":{\"null\":null,\"time\":\"2023-01-16 17:39:53\",\"long\":\"12\",\"int\":12}}".equals(output);
    }
}
