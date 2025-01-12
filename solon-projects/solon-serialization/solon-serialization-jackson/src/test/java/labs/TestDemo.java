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
package labs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;


/**
 * @author noear 2021/10/12 created
 */
@SolonTest(TestApp.class)
public class TestDemo extends HttpTester {
    @Test
    public void test0() throws Exception{
        String json = path("/").get();

        JsonNode oNode = new ObjectMapper().readTree(json);


        assert  oNode.get("time1").asText().length() == 16;
        assert  oNode.get("time2").asText().length() == 10;
        assert  oNode.get("time3").asLong() > 1000000000;
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
}
