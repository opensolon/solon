package test3;

import features2.model.UserDo;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.solon.core.handle.ContextEmpty;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 什么配置都没有
 */
public class TestQuickConfig {

    @Test
    public void hello2() throws Throwable{
        UserDo userDo = new UserDo();

        Map<String, Object> data = new HashMap<>();
        data.put("time", new Date(1673861993477L));
        data.put("long", 12L);
        data.put("int", 12);
        data.put("null", null);

        userDo.setMap1(data);

        String output = ApiReturnJsonUtil.toJson(userDo);

        System.out.println(output);

        assert ONode.load(output).count() == 5;

        //完美
        assert "{\"b0\":false,\"b1\":true,\"d0\":0,\"d1\":1.0,\"list0\":[],\"map0\":null,\"map1\":{\"null\":null,\"time\":\"2023-01-16 17:39:53\",\"long\":\"12\",\"int\":12},\"n0\":0,\"n1\":\"1\",\"obj0\":null,\"s0\":\"\",\"s1\":\"noear\"}".equals(output);
    }
}
