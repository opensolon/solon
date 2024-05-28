package features.test3;

import features.model.UserDo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Import;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.serialization.jackson.xml.JacksonXmlRenderFactory;
import org.noear.solon.test.SolonJUnit5Extension;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2023/1/16 created
 */
@Import(profiles = "classpath:features2_test3.yml")
@ExtendWith(SolonJUnit5Extension.class)
public class TestQuickConfig {
    @Inject
    JacksonXmlRenderFactory renderFactory;

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

        //error 会有多余的 null (和所有 null 打印开启一样)

        assert "<UserDo><b0>0</b0><b1>true</b1><d0>0.0</d0><d1>1.0</d1><list0/><map0/><map1><null/><time>2023-01-16 17:39:53</time><long>12</long><int>12</int></map1><n0>0</n0><n1>1</n1><obj0/><s0></s0><s1>noear</s1></UserDo>".equals(output);
    }
}
