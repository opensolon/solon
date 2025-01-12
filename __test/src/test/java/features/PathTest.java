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
import org.noear.solon.core.util.PathMatcher;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;

public class PathTest {
    @Test
    public void test() {
        assert PathUtil.mergePath("/user/*", "").equals("/user/");
        assert PathUtil.mergePath("", "/user/*").equals("/user/*");
        assert PathUtil.mergePath("/", "/user/*").equals("/user/*");
        assert PathUtil.mergePath("/render/direct/", "*").equals("/render/direct/*");
        assert PathUtil.mergePath("user", "/").equals("/user/");
        assert PathUtil.mergePath("user", "").equals("/user");
        assert PathUtil.mergePath("user/add", "/").equals("/user/add/");
    }

    @Test
    public void test2() {
        StaticMappings.add("/a/", new FileStaticRepository("/test/"));
    }

    @Test
    public void test3() {
        PathMatcher pathAnalyzer = PathMatcher.get("/demo2/intercept/**");

        assert pathAnalyzer.matches("/demo2/intercept/");
    }

    @Test
    public void test4() {
        PathMatcher pathAnalyzer = PathMatcher.get("/demo2/intercept/*");

        assert pathAnalyzer.matches("/demo2/intercept/");
    }
}
