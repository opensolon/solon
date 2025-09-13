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
package labs.serialization.fastjson2.labs1;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import features.serialization.fastjson2.model.UserDo;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;


/**
 * @author noear 2021/10/12 created
 */
@SolonTest(TestApp.class)
public class TestDemo extends HttpTester {
    @Test
    public void home_test() throws Exception {
        String json = path("/").get();

        JSONObject oNode = JSON.parseObject(json);

        assert oNode.getString("time1").length() == 16;
        assert oNode.getString("time2").length() == 10;
        assert oNode.getLong("time3") > 1000000000;
    }

    @Test
    public void hello_test() throws Exception {
        String json = path("/hello").bodyOfJson("").post();
        assert "".equals(json);

        json = path("/hello?name=world").bodyOfJson("").post();
        assert "world".equals(json);

        json = path("/hello").bodyOfJson("{\"name\":\"world\"}").post();
        assert "world".equals(json);
    }

    @Test
    public void dubble_as_string() {
        UserDo userDo = new UserDo();
        String json = JSON.toJSONString(userDo, JSONWriter.Feature.WriteNullNumberAsZero, JSONWriter.Feature.WriteLongAsString);
        System.out.println(json);
    }
}
