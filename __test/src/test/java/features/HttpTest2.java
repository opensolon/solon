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

/**
 * @author noear 2020/12/24 created
 */
@SolonTest(App.class)
public class HttpTest2 extends HttpTester {
    @Test
    public void test1() throws IOException {
        assert path("/demo1/run1/*?@=1").get().equals("http://localhost:8080/demo1/run1/*");
    }

    @Test
    public void test2() throws IOException {
        assert path("/demo1/run3/*?@=1").get().equals("@=1");
    }

    @Test
    public void test404() throws IOException {
        assert path("/demo1/hello/").head() == 404;
    }

//    @Test
//    public void test3() throws IOException {
//        if (ClassUtil.loadClass("javax.servlet.http.HttpServletRequest") != null) {
//            assert path("/demo2/servlet/hello?name=noear").get().equals("Ok");
//        }
//    }

}
