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

/**
 * @author noear 2021/7/22 created
 */
@SolonTest(App.class)
public class ViewTest extends HttpTester {
    @Test
    public void test90() throws IOException {
        String rst = path("/demo9/view/json").get();
        assert  rst.indexOf("dock") > 0;
        assert  rst.indexOf("world") > 0;
    }

    @Test
    public void test91() throws IOException {
        String rst = path("/demo9/view/beetl").get();
        assert  rst.indexOf("beetl::") > 0;
        assert  rst.indexOf("你好") > 0;
    }

    @Test
    public void test92() throws IOException{
        String rst = path("/demo9/view/ftl").get();
        assert  rst.indexOf("ftl::") > 0;
        assert  rst.indexOf("你好") > 0;
    }

    @Test
    public void test93() throws IOException{
        String rst = path("/demo9/view/enjoy").get();
        assert  rst.indexOf("enjoy::") > 0;
        assert  rst.indexOf("你好") > 0;
    }

    @Test
    public void test94() throws IOException{
        String rst = path("/demo9/view/thymeleaf").get();
        assert  rst.indexOf("thymeleaf::") > 0;
        assert  rst.indexOf("你好") > 0;
    }

    @Test
    public void test95() throws IOException{
        String rst = path("/demo9/view/velocity").get();
        assert  rst.indexOf("velocity::") > 0;
        assert  rst.indexOf("你好") > 0;
    }
}
