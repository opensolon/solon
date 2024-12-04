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
import org.noear.solon.Solon;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.dso.gtype.Parent;
import webapp.dso.gtype.S1;
import webapp.dso.gtype.S2;

/**
 * @author noear 2022/1/17 created
 */
@SolonTest(App.class)
public class GtypeTest {
    @Test
    public void test1() {
        Parent o = Solon.context().getBean(S1.class);
        Parent o2 = Solon.context().getBean(S2.class);

        assert 1 == o.hello();
        assert 2 == o2.hello();
    }
}
