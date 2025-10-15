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
import org.noear.snack4.ONode;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/6/15 created
 */
@SolonTest(App.class)
public class HttpValidTest2 extends HttpTester {

    @Test
    public void test0() throws IOException {
        assert path("/demo2/valid/bean2").get().contains("field1");
    }

    @Test
    public void test0_1() throws IOException {
        assert path("/demo2/valid/bean2").bodyOfJson("").post().contains("field1");
    }

    @Test
    public void test1() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field2");

        data.put("field1", "2020-12-12 12:12:12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field1");
    }

    @Test
    public void test2() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");

        assert path("/demo2/valid/bean2").data(data).post().contains("field3");

        data.put("field2", "2020-12-12 12:12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field2");
    }

    @Test
    public void test3() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");

        assert path("/demo2/valid/bean2").data(data).post().contains("field4");

        data.put("field3", "12.0");
        assert path("/demo2/valid/bean2").data(data).post().contains("field3");
    }


    @Test
    public void test4() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");

        assert path("/demo2/valid/bean2").data(data).post().contains("field5");

        data.put("field4", "9.0");
        assert path("/demo2/valid/bean2").data(data).post().contains("field4");
    }


    @Test
    public void test5() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");

        assert path("/demo2/valid/bean2").data(data).post().contains("field6");

        data.put("field5", "noear.live.cn");
        assert path("/demo2/valid/bean2").data(data).post().contains("field5");
    }

    @Test
    public void test6() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");

        assert path("/demo2/valid/bean2").data(data).post().contains("field7");

        data.put("field6", "noear@cc.cn");
        assert path("/demo2/valid/bean2").data(data).post().contains("field6");
    }

    @Test
    public void test7() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");

        assert path("/demo2/valid/bean2").data(data).post().contains("field8");

        data.put("field7", "12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field7");
    }

    @Test
    public void test8() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");

        assert path("/demo2/valid/bean2").data(data).post().contains("field9");

        data.put("field8", "9");
        assert path("/demo2/valid/bean2").data(data).post().contains("field8");
    }

    @Test
    public void test9() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");

        assert path("/demo2/valid/bean2").data(data).post().contains("field10");

        data.put("field9", " ");
        assert path("/demo2/valid/bean2").data(data).post().contains("field9");
    }

    @Test
    public void test10() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");

        assert path("/demo2/valid/bean2").data(data).post().contains("field11");

        data.put("field10", "");
        assert path("/demo2/valid/bean2").data(data).post().contains("field10");
    }

    @Test
    public void test11() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");

        assert path("/demo2/valid/bean2").data(data).post().contains("field12");

        data.remove("field11");
        assert path("/demo2/valid/bean2").data(data).post().contains("field11");
    }

    @Test
    public void test12() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");
        data.put("field12", "1");

        assert path("/demo2/valid/bean2").data(data).post().contains("field15");

        data.remove("field12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field12");
    }

    @Test
    public void test15() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");
        data.put("field12", "1");
        data.put("field15", "111-12");

        assert path("/demo2/valid/bean2").data(data).post().contains("field16");

        data.put("field15", "111a-12");
        assert path("/demo2/valid/bean2").data(data).post().contains("field15");
    }

    @Test
    public void test16() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");
        data.put("field12", "1");
        data.put("field15", "111-12");
        data.put("field16", "1");

        assert path("/demo2/valid/bean2").data(data).post().contains("OK");

        data.put("field16", "0");
        assert path("/demo2/valid/bean2").data(data).post().contains("field16");
    }

    @Test
    public void test17() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");
        data.put("field12", "1");
        data.put("field15", "111-12");
        data.put("field16", "1");
        data.put("field17", "1xxxx");

        assert path("/demo2/valid/bean2").data(data).post().contains("OK");

        data.put("field17", "0");
        assert path("/demo2/valid/bean2").data(data).post().contains("field17");
    }

    @Test
    public void test18() throws IOException {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("field1", "2020-12-12T12:12:12");
        data.put("field2", "2020-12-12");
        data.put("field3", "9.0");
        data.put("field4", "12.0");
        data.put("field5", "noear@live.cn");
        data.put("field6", "noear@live.cn");
        data.put("field7", "9");
        data.put("field8", "12");
        data.put("field9", "x");
        data.put("field10", " ");
        data.put("field11", "1");
        data.put("field12", "1");
        data.put("field15", "111-12");
        data.put("field16", "1");
        data.put("field17", "1xxxx");

        ONode node = ONode.ofBean(data);
        node.getOrNew("field18").add("1").add("2");

        assert path("/demo2/valid/bean2").bodyOfJson(node.toJson()).post().contains("OK");

        node.get("field18").clear();
        node.get("node").add("1");

        assert path("/demo2/valid/bean2").bodyOfJson(node.toJson()).post().contains("field18");
    }

    @Test
    public void test_err1() throws IOException {
        assert path("/demo2/valid/err1").bodyOfJson("{}").post().contains("OK") == false;
    }

    @Test
    public void test_err2() throws IOException {
        assert path("/demo2/valid/err2").get().contains("OK") == false;
    }
}
