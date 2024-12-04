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
package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SolonTest(App.class)
public class HttpMethod2Test extends HttpTester {

    @Test
    public void test21() throws IOException {
        assert path("/demo2/method2/post").execAsCode("GET") == 405;
        assert path("/demo2/method2/post_xxx").execAsCode("GET") == 404;
    }

    @Test
    public void test22_post() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert path("/demo2/method2/post").data(map).post().equals("中文");
    }

    @Test
    public void test23_head() throws IOException {
        assert path("/demo2/method2/post_get").execAsCode("HEAD") == 200;
    }

    @Test
    public void test23_get() throws IOException {
        assert path("/demo2/method2/post_get").get().equals("/demo2/method2/post_get");
    }

    @Test
    public void test23_post() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert path("/demo2/method2/post_get").data(map).post().equals("/demo2/method2/post_get");
    }

    @Test
    public void test24_put() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert path("/demo2/method2/put").data(map).put().equals("中文");
    }

    @Test
    public void test24_delete() throws IOException {
        //delete ，有些 server 只支持 queryString param
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert path("/demo2/method2/delete").data(map).delete().equals("中文");
    }

    @Test
    public void test24_delete_2() throws IOException {
        assert path("/demo2/method2/delete?name=中文").delete().equals("中文");
    }

    @Test
    public void test24_patch() throws IOException {
        Map<String, String> map = new HashMap<>();
        map.put("name", "中文");

        assert path("/demo2/method2/patch").data(map).patch().equals("中文");
    }
}
