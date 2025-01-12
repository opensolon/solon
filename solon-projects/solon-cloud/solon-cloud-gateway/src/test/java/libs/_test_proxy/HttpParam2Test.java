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

import java.io.IOException;

/**
 * @author noear 2020/12/20 created
 */
@SolonTest(App.class)
public class HttpParam2Test extends HttpTester {
    @Override
    public boolean enablePrint() {
        return true;
    }

    @Test
    public void test1_required() throws IOException {
        assert path("/demo2/param2/anno/required").execAsCode("GET") == 400;

        assert path("/demo2/param2/anno/required?name=hi").execAsCode("GET") != 400;
    }

    @Test
    public void test2() throws IOException {
        assert path("/demo2/param2/anno/required_def").get().equals("noear");
    }

    @Test
    public void test3_def() throws IOException {
        assert path("/demo2/param2/anno/def").get().equals("noear");
    }

    @Test
    public void test4() throws IOException {
        assert path("/demo2/param2/anno/name?n2=noear").get().equals("noear");

        assert path("/demo2/param2/anno/name?n2=hi").get().equals("hi");
    }
}
