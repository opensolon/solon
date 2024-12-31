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
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;

/**
 * @author noear 2023/3/19 created
 */
@SolonTest(App.class)
public class ResourceFileTest {
    static final String file_root_dir = "file:/Users/noear/WORK/work_github/noear/solon/__test/target/classes/";

    @Test
    public void scanResources() {
        Collection<String> paths = ResourceUtil.scanResources(file_root_dir + "static_test/**/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources(file_root_dir + "static_test/dir1/*/b.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources(file_root_dir + "static_test/dir1/*/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources(file_root_dir + "static_test/dir1/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources(file_root_dir + "static_test/**/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 2;
    }

    @Test
    public void scanResources2() {
        Collection<String> paths = ResourceUtil.scanResources(file_root_dir + "/static_test/**/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources(file_root_dir + "/static_test/dir1/*/b.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources(file_root_dir + "/static_test/dir1/*/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources(file_root_dir + "/static_test/dir1/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources(file_root_dir + "/static_test/**/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 2;
    }

    @Test
    public void findResource() throws IOException {
        URL url = ResourceUtil.findResource(file_root_dir + "app.yml");
        assert url != null;
        System.out.println(url);
    }
}
