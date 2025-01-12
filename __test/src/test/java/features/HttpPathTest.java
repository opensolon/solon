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

/**
 * @author noear 2022/12/10 created
 */
@SolonTest(App.class)
public class HttpPathTest extends HttpTester {
    @Test
    public void test0() throws Exception {
        assert path("/demo2/path/test0").get().equals("ok");
        assert path("/demo2/path/test0/").get().equals("ok");
        assert path("/demo2/path/test0/a").get().equals("ok");
        assert path("/demo2/path/test0/a/b").get().equals("ok");
    }

    @Test
    public void test1() throws Exception {
        assert path("/demo2/path/test1").get().equals("ok");
        assert path("/demo2/path/test1/").get().equals("ok");
        assert path("/demo2/path/test1/a").get().equals("ok") == false;
        assert path("/demo2/path/test1/a/b").get().equals("ok") == false;
    }

    @Test
    public void test2() throws Exception {
        assert path("/demo2/path/test2").get().equals("ok");
        assert path("/demo2/path/test2/").get().equals("ok");
        assert path("/demo2/path/test2/a").get().equals("ok");
        assert path("/demo2/path/test2/a/b").get().equals("ok") == false;
    }


    @Test
    public void test3() throws Exception {
        assert path("/demo2/path/test3/solon").get().equals("ok");
        assert path("/demo2/path/test3/solon").data("name", "noear").post().equals("ok");
    }

    @Test
    public void test3_b() throws Exception {
        assert path("/demo2/path/test3/b").data("name", "noear").post().equals("noear");
    }
}
