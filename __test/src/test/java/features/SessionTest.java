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
package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.util.List;

/**
 * @author noear 2021/2/15 created
 */
@SolonTest(App.class)
public class SessionTest extends HttpTester {
    @Test
    public void test() throws Exception {
        HttpResponse response = path("/demob/session/setval").exec("get");
        System.out.println(response.bodyAsString());

        List<String> cookies = response.cookies();

        HttpUtils httpUtils = path("/demob/session/getval");

        StringBuilder sb = new StringBuilder();
        for (String c1 : cookies) {
            String kv = c1.split(";")[0];
            if (kv.contains("=")) {
                sb.append(kv).append(";");
            }
        }

        if(sb.length() > 0) {
            System.out.println(sb.toString());
            httpUtils.header("Cookie", sb.toString());
        }

        String html = httpUtils.get();

        assert html.contains("121212");
    }
}
