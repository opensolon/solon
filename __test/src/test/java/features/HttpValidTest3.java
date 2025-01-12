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
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/9/5 created
 */
@SolonTest(App.class)
public class HttpValidTest3 extends HttpTester {
    @Test
    public void test0() throws IOException {
        assert path("/demo2/valid/bean3").get().contains("field10");

        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1","");
        assert path("/demo2/valid/bean3").data(data).post().contains("field10");
    }

    @Test
    public void test1() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1","2020-12-12T12:12:12");
        assert path("/demo2/valid/bean3").data(data).post().contains("field10");

        data.put("field1","2020-12-12 12:12:12");
        assert path("/demo2/valid/bean3").data(data).post().contains("field1");
    }

    @Test
    public void test10() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1","");
        data.put("field10","2020-12-12T12:12:12");
        assert path("/demo2/valid/bean3").data(data).post().contains("field2");

        data.put("field10","");
        assert path("/demo2/valid/bean3").data(data).post().contains("field10");
    }

    @Test
    public void test2() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1","");
        data.put("field10","2020-12-12T12:12:12");
        data.put("field2","noear@live.cn");
        assert path("/demo2/valid/bean3").data(data).post().contains("field20");

        data.put("field2","");
        assert path("/demo2/valid/bean3").data(data).post().contains("field20");


        data.put("field20","noear@live.cn");
        assert path("/demo2/valid/bean3").data(data).post().contains("field30");
    }

    @Test
    public void test3() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field10","2020-12-12T12:12:12");
        data.put("field20","noear@live.cn");
        data.put("field3","");
        assert path("/demo2/valid/bean3").data(data).post().contains("field30");

        data.put("field30","333-23333");
        assert path("/demo2/valid/bean3").data(data).post().contains("OK");
    }

    @Test
    public void test_bean4() throws IOException {
        assert path("/demo2/valid/bean4?mobile=x&password=x&id=1").get().equals("OK");
        assert path("/demo2/valid/bean4?mobile=x&password=x").get().equals("OK");
    }

    @Test
    public void test_bean4_update() throws IOException {
        assert path("/demo2/valid/bean4_update?mobile=x&password=x&id=1").get().equals("OK");
        assert path("/demo2/valid/bean4_update?mobile=x&password=x").get().equals("OK") == false;
    }
}
