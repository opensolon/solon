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
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo7_test.DynamicService;

/**
 * @author noear 2023/9/1 created
 */
@SolonTest(App.class)
public class DataTest {

    @Inject
    DynamicService dynamicService;

    @Test
    public void test1() throws Exception{
        assert  "db_rock1".equals(dynamicService.test1());
        assert  "db_rock2".equals(dynamicService.test2());
        assert  "".equals(dynamicService.test3());
    }
}
