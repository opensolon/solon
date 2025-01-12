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
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2023/2/16 created
 */
@SolonTest
public class CacheTest {
    @Inject
    CacheService cacheService;

    @Test
    public void test() throws Exception {
        cacheService.store("test", "1", 1);
        assert "1".equals(cacheService.get("test", String.class));
        Thread.sleep(2000);
        assert cacheService.get("test", String.class) == null;


        UserM userM = new UserM();
        userM.id = 12;
        userM.name = "test";

        cacheService.store("test", userM, 1);
        assert userM.id == cacheService.get("test", UserM.class).id;
        Thread.sleep(2000);
        assert cacheService.get("test", UserM.class) == null;
    }

    @Test
    public void test2() throws Exception {
        cacheService.store("test", "1", 0);
        assert "1".equals(cacheService.get("test", String.class));
        cacheService.remove("test");
        assert cacheService.get("test", String.class) == null;


        UserM userM = new UserM();
        userM.id = 12;
        userM.name = "test";

        cacheService.store("test", userM, 0);
        assert userM.id == cacheService.get("test", UserM.class).id;
        cacheService.remove("test");
        assert cacheService.get("test", UserM.class) == null;
    }
}
