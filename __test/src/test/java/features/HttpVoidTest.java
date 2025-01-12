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
 * @author noear 2024/12/31 created
 */
@SolonTest(App.class)
public class HttpVoidTest extends HttpTester {
    @Test
    public void void1() throws Exception {
        assert "".equals(path("/demo2/void1").get());
    }

    @Test
    public void void2() throws Exception {
        assert "".equals(path("/demo2/void2").get()) == false;
    }
}
