/*
 * Copyright 2017-2024 noear.org and authors
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
package libs.gateway1;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/8/16 created
 */
@SolonTest(Gateway1Main.class)
public class Gateway1Test extends HttpTester {
    @Test
    public void WebTest() throws Exception {
        String rst = path("/hello?name=noear").get();
        assert rst != null;
        assert rst.equals("noear");
    }

    @Test
    public void GatewayGetTest() throws Exception {
        HttpResponse resp = path("/demo/test?name=noear").exec("GET");

        assert "1".equals(resp.header("Test-V"));

        String rst = resp.bodyAsString();
        assert rst != null;
        assert rst.equals("noear");
    }

    @Test
    public void GatewayPostTest() throws Exception {
        String rst = path("/demo/test?p1=1").data("name", "noear").post();
        assert rst != null;
        assert rst.equals("noear");
    }

    @Test
    public void GatewayPostBodyTest() throws Exception {
        String rst = path("/demo/test").bodyJson("{\"name\":\"noear\"}").post();
        assert rst != null;
        assert rst.equals("noear");
    }
}
