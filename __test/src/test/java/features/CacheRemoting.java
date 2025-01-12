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
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo2_cache.RemotingService;

/**
 * @author noear 2022/3/22 created
 */
@SolonTest(App.class)
public class CacheRemoting {
    @NamiClient(url = "tcp://localhost:28080/demo2/rmt/sev")
    RemotingService remotingService;

    @Test
    public void cache() throws Exception{
        String tmp = remotingService.hello("noear");
        Thread.sleep(1000);

        String tmp2 = remotingService.hello("noear");

        assert tmp2.equals(tmp);
    }
}
