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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.solon.server.handle.HeaderNames;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SolonTest(App.class)
public class HeaderTest extends HttpTester {
    @Test
    public void test1() throws Exception {
        assert path("/demo1/run0/?str=").get().equals("不是null(ok)");

        Map<String, String> map = new LinkedHashMap<>();
        map.put("address", "192.168.1.1:9373");
        map.put("service", "wateradmin");
        map.put("meta", "");
        map.put("check_type", "0");
        map.put("is_unstable", "0");
        map.put("check_url", "/_run/check/");

        assert path("/demo2/header/")
                .header("Water-Trace-Id", "")
                .header("Water-From", "wateradmin@192.168.1.1:9373")
                .data(map).post().equals("");
    }

    @Test
    public void test1_header2() throws Exception {
        String json = path("/demo2/header2/")
                .headerAdd("test", "a")
                .headerAdd("test", "b")
                .get();

        assert json.length() > 0;
        assert json.contains("a");
        assert json.contains("b");
    }

    @Test
    public void test1_remote() throws IOException {
        String json = path("/demo2/remote/").get();

        assert json.length() > 0;
        ONode oNode = ONode.load(json);
        assert oNode.isArray();
        assert oNode.get(1).val().getRaw() instanceof Number;
        assert oNode.get(1).getInt() > 80;
    }

    @Test
    public void test2() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("address", "192.168.1.1:9373");
        map.put("service", "wateradmin");
        map.put("meta", "");
        map.put("check_type", "0");
        map.put("is_unstable", "0");
        map.put("check_url", "/_run/check/");

        assert path("/demo2/header/")
                .header("Water-Trace-Id", "a")
                .header("Water-From", "wateradmin@192.168.1.1:9373")
                .data(map).post().equals("a");
    }

    @Test
    public void test3() throws Exception {
        HttpResponse res = path("/demo2/cookie/").exec("GET");

        List<String> tmp = res.headers("Set-Cookie");

        assert tmp.size() >= 2;
    }

    @Test
    public void testContentLength() throws Exception {
        HttpResponse res = path("/demo1/header/hello").exec("GET");

        List<String> tmp = res.headers(HeaderNames.HEADER_CONTENT_LENGTH);
        assert tmp != null;
        assert tmp.size() == 1;
        long size = Long.parseLong(tmp.get(0));
        byte[] bytes = res.bodyAsBytes();
        assert size == bytes.length;
        assert "Hello world!".equals(new String(bytes));
    }

    @Test
    public void testContentType_post_form() throws Exception {
        String rst = path("/demo2/header/ct").data("name", "solon").post();
        assert rst.equals("POST::application/x-www-form-urlencoded::solon");
    }

    @Test
    public void testContentType_post_multipart() throws Exception {
        String rst = path("/demo2/header/ct").data("name", "solon").multipart(true).post();
        assert rst.startsWith("POST::multipart/form-data");
        assert rst.endsWith("::solon");
        assert rst.equals("POST::multipart/form-data::solon") == false;


        rst = path("/demo2/header/ct?name=solon").get();
        assert rst.equals("GET::null::solon");
    }

    @Test
    public void testContentType_get() throws Exception {
        String rst = path("/demo2/header/ct?name=solon").get();
        Assertions.assertEquals("GET::null::solon", rst);
    }

    @Test
    public void testServer_get() throws Exception {
        String rst = path("/demo2/header/server").exec("GET").header("Server");
        Assertions.assertNull(rst);

        rst = path("/demo2/header/server?out=1").exec("GET").header("Server");
        Assertions.assertEquals("solon", rst);
    }

    @Test
    public void testList() throws Exception {
        String list = path("/demo2/header/list")
                .header("X-Test","1")
                .accept("application/json")
                .get();

        assert list.length() > 0;
        assert list.contains("X-Test");
        assert list.contains("Accept");
        assert list.contains("application/json");
    }
}