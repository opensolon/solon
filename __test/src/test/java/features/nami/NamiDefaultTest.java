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
package features.nami;

import org.junit.jupiter.api.Test;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2022/11/7 created
 */
@SolonTest(App.class)
public class NamiDefaultTest {

    @NamiClient(url = "https://api.github.com")
    GitHub gitHub;

    @Test
    public void test() {
        System.out.println(gitHub.hashCode());
        assert gitHub.hashCode() > 0;

        System.out.println(gitHub.hello());
        assert "hello".equals(gitHub.hello());

        System.out.println(gitHub.toString());
        assert (GitHub.class.getName() + ".$Proxy").equals(gitHub.toString());
    }
}
