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
package labs.serialization.gson;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

import java.util.Map;


/**
 * @author noear 2021/10/12 created
 */
@SolonTest(TestApp.class)
public class TestDemo extends HttpTester {
    @Test
    public void test0() throws Exception{
        String json = path("/").get();

        Map oNode = new Gson().fromJson(json, Map.class);

        assert  ((String)oNode.get("time1")).length() == 16;
        assert  ((String)oNode.get("time2")).length() == 10;
        assert  ((Number)oNode.get("time3")).longValue() > 1000000000;
    }
}
