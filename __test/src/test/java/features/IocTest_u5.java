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
import webapp.demo6_aop.Bean2;

@SolonTest(App.class)
public class IocTest_u5 {
    @Inject
    Bean2 bean2;

    @Inject("${testpath}")
    String testpath;

    //双向依赖的bean测试
    //
    @Test
    public void test() {
        assert "bean2bean1".equals(bean2.namePlus());
        assert "D:/abc-1-2.12/xx.xml".equals(testpath);
        System.out.println(testpath);
    }
}
