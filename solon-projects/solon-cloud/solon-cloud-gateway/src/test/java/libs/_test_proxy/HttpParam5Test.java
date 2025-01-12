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
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2021/1/5 created
 */
@SolonTest(App.class)
public class HttpParam5Test extends HttpTester {
    @Test
    public void test1() throws Exception {
        String rst = path("/demo2/param5/test1?a=1&params[a]=2").get();

        assert "1:2".equals(rst);
    }

    @Test
    public void test2() throws Exception {
        String rst = path("/demo2/param5/test2?cat=2").get();

        assert "demo2".equals(rst);

        rst = path("/demo2/param5/test2?cat=1").get();

        assert "demo1".equals(rst);
    }

    @Test
    public void test3() throws Exception {
        String rst = path("/demo2/param5/test3?cat=2").get();

        assert "demo2".equals(rst);

        rst = path("/demo2/param5/test3?cat=1").get();

        assert "demo1".equals(rst);
    }

    private static final String test4_rst1 = "{\"a\":[\"1\"],\"b\":[\"2\"]}";
    private static final String test4_rst2 = "{\"b\":[\"2\"],\"a\":[\"1\"]}";

    @Test
    public void test4() throws Exception {
        String rst = path("/demo2/param5/test4?a=1").data("b", "2").post();
        assert test4_rst1.equals(rst) || test4_rst2.equals(rst);

        rst = path("/demo2/param5/test4?a=1").data("b", "2").multipart(true).post();
        assert test4_rst1.equals(rst) || test4_rst2.equals(rst);
    }

    @Test
    public void test4_2() throws Exception {
        String rst = path("/demo2/param5/test4?a=1").data("b", "2").delete();
        assert test4_rst1.equals(rst) || test4_rst2.equals(rst);

        rst = path("/demo2/param5/test4?a=1").data("b", "2").multipart(true).delete();
        assert test4_rst1.equals(rst) || test4_rst2.equals(rst);
    }

    @Test
    public void test5() throws Exception {
        String rst = path("/demo2/param5/test5?name=1").get();

        assert "postArguments".equals(rst);
    }
}
