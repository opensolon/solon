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
package libs._test_proxy;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

import java.io.IOException;
import java.util.List;

/**
 * @author noear 2022/3/30 created
 */
@SolonTest(App.class)
public class HttpSessionTest extends HttpTester {

    @Test
    public void session() throws IOException {
        //init
        HttpResponse response = path("/demo2/session/id").exec("GET");
        String session_id = response.bodyAsString();
        List<String> cookies;
        StringBuilder cookiesStr;

        cookies = response.cookies();
        cookiesStr = new StringBuilder();
        for (String cookie : cookies) {
            String KeyVal = cookie.split(";")[0];
            System.out.println("cookie:: " + KeyVal);
            if (KeyVal.contains("=")) {
                cookiesStr.append(KeyVal).append("; ");
            }
        }


        System.out.println(session_id);
        System.out.println(cookiesStr);


        //set
        response = path("/demo2/session/set").data("val", "test")
                .header("Cookie", cookiesStr.toString())
                .exec("POST");

        cookies = response.headers("Set-Cookie");
        cookiesStr = new StringBuilder();
        for (String cookie : cookies) {
            String KeyVal = cookie.split(";")[0];
            System.out.println("cookie:: " + KeyVal);
            if (KeyVal.contains("=")) {
                cookiesStr.append(KeyVal).append("; ");
            }
        }

        //get
        String rst = path("/demo2/session/get")
                .header("Cookie", cookiesStr.toString())
                .get();
        assert "test".equals(rst);
    }

    @Test
    public void session2() throws IOException {
        //init
        HttpResponse response = path("/demo2/session/id").exec("GET");
        String session_id = response.bodyAsString();
        List<String> cookies;
        StringBuilder cookiesStr;

        cookies = response.cookies();
        cookiesStr = new StringBuilder();
        for (String cookie : cookies) {
            String KeyVal = cookie.split(";")[0];
            System.out.println("cookie:: " + KeyVal);
            if (KeyVal.contains("=")) {
                cookiesStr.append(KeyVal).append("; ");
            }
        }


        System.out.println(session_id);
        System.out.println(cookiesStr);


        //set
        path("/demo2/session/set").data("val", "test")
                .exec("POST");


        //get（使用旧的 cookie）
        String rst = path("/demo2/session/get")
                .get();
        assert "test".equals(rst) == false;
    }
}
