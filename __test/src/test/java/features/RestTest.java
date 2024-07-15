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
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2021/11/25 created
 */
@SolonTest(App.class)
public class RestTest extends HttpTester {
    @Test
    public void null0() throws Exception {
        assert path("/demo2a/rest/null").get().equals("");
        assert path("/demo2a/rest/null").exec("GET").header("test") == null;
    }

    @Test
    public void get() throws Exception {
        assert path("/demo2a/rest/test?name=noear").get().equals("Get-noear");
    }

    @Test
    public void post() throws Exception {
        assert path("/demo2a/rest/test").data("name", "noear").post().equals("Post-noear");
    }

    @Test
    public void put() throws Exception {
        assert path("/demo2a/rest/test").data("name", "noear").put().equals("Put-noear");
    }

    @Test
    public void delete() throws Exception {
        assert path("/demo2a/rest/test").data("name", "noear").delete().equals("Delete-noear");
    }

    @Test
    public void patch() throws Exception {
        assert path("/demo2a/rest/test").data("name", "noear").patch().equals("Patch-noear");
    }
}
