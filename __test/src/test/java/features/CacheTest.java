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

@SolonTest(App.class)
public class CacheTest extends HttpTester {
    @Test
    public void test1() throws Exception {
        String rst = path("/cache/").get();

        Thread.sleep(100);

        assert rst.equals(path("/cache/").get());

        Thread.sleep(100);

        assert rst.equals(path("/cache/").get());

        Thread.sleep(100);

        assert rst.equals(path("/cache/").get());
    }

    @Test
    public void test2() throws Exception {
        String rst = path("/cache/").get();

        Thread.sleep(100);

        rst = path("/cache/update").get();
        assert rst.equals(path("/cache/").get());

        Thread.sleep(100);

        rst = path("/cache/update").get();
        assert rst.equals(path("/cache/").get());

        Thread.sleep(100);

        rst = path("/cache/update").get();
        assert rst.equals(path("/cache/").get());
    }

    @Test
    public void test3() throws Exception {
        String rst = path("/cache/").get();

        Thread.sleep(100);

        path("/cache/remove").get();
        assert rst.equals(path("/cache/").get()) == false;

        Thread.sleep(100);

        path("/cache/remove").get();
        assert rst.equals(path("/cache/").get()) == false;

        Thread.sleep(100);

        path("/cache/remove").get();
        assert rst.equals(path("/cache/").get()) == false;
    }

    @Test
    public void test_error() throws Exception {
        int code = path("/cache/error").head();
        System.out.println(code);
        assert code != 200;
        int code2 = path("/cache/error").execAsCode("GET");
        assert code2 == 500;
    }
}
