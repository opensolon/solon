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
 * @author noear 2023/10/10 created
 */
@SolonTest(App.class)
public class HttpExtTest extends HttpTester {
    @Test
    public void ext_gt() throws Exception {
        String rst = path("/demo2/ext/hello").data("name","solon").post();

        assert rst != null;
        assert rst.contains("solon");
    }

    @Test
    public void ext_gt2() throws Exception {
        String rst = path("/demo2/ext/save").bodyOfJson("{id:1,name:'solon'}").post();

        assert rst != null;
        assert rst.contains("solon");
    }
}
