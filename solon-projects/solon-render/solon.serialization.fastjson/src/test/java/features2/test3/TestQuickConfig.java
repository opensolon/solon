package features2.test3;

import features2.model.UserDo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.fastjson.FastjsonRenderFactory;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.annotation.TestPropertySource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间进行格式化 + long,int 转为字符串 + 常见类型转为非null
 */
@TestPropertySource("classpath:features2_test3.yml")
@RunWith(SolonJUnit4ClassRunner.class)
public class TestQuickConfig {
    @Inject
    FastjsonRenderFactory renderFactory;

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

        //开始出现 null （有 nullAs?? 时，出现了 null;  long 型 null 的没有转成字符串 "0"）
        assert "{\"b0\":false,\"b1\":true,\"d0\":0,\"d1\":1.0,\"list0\":[],\"map0\":null,\"map1\":{\"time\":\"2023-01-16 17:39:53\",\"long\":\"12\",\"int\":12},\"n0\":0,\"n1\":\"1\",\"obj0\":null,\"s0\":\"\",\"s1\":\"noear\"}".equals(output);
    }
}
