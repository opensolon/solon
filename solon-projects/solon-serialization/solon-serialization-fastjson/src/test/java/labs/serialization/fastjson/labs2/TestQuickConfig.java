/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package labs.serialization.fastjson.labs2;

import features.serialization.fastjson.model.UserDo;
import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;

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

        assert ONode.ofJson(output).size() == 5;

        //完美
        assert "{\"b0\":false,\"b1\":true,\"d0\":0,\"d1\":1.0,\"list0\":[],\"map0\":null,\"map1\":{\"null\":null,\"time\":\"2023-01-16 17:39:53\",\"long\":\"12\",\"int\":12},\"n0\":0,\"n1\":\"1\",\"obj0\":null,\"s0\":\"\",\"s1\":\"noear\"}".equals(output);
    }
}
