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
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Props;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2024/4/10 created
 */
@SolonTest(App.class)
public class AppContextTest {
    @Test
    public void put_del() {
        AppContext context = new AppContext(new AppClassLoader(), new Props());
        context.putWrap("test", context.wrapAndPut(String.class, "test"));
        assert context.getBean("test") != null;

        context.removeWrap("test");
        assert context.getBean("test") == null;
    }
}
