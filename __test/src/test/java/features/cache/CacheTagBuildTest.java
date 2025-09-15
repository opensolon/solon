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
package features.cache;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.dso.cache.OathServer;
import webapp.dso.cache.Oauth;

/**
 * @author noear 2022/1/15 created
 */
@SolonTest(App.class)
public class CacheTagBuildTest {
    @Inject
    OathServer oathServer;

    @Test
    public void test_by_rst() throws InterruptedException{
        Oauth oauth = oathServer.queryInfoByCode("12");
        Oauth oauth2 = oathServer.queryInfoByCode("12");

        System.out.println(oauth.getExpTime() + " : " + oauth2.getExpTime());
        assert oauth.getExpTime().equals(oauth2.getExpTime());

        oathServer.updateInfo2(oauth);
        Thread.sleep(10);

        Oauth oauth3 = oathServer.queryInfoByCode("12");

        System.out.println(oauth.getExpTime() + " : " + oauth3.getExpTime());
        assert oauth.getExpTime().equals(oauth3.getExpTime()) == false;
    }

    @Test
    public void test_by_param_bean() throws InterruptedException{
        Oauth oauth = oathServer.queryInfoByCode("12");
        Oauth oauth2 = oathServer.queryInfoByCode("12");

        System.out.println(oauth.getExpTime() + " : " + oauth2.getExpTime());
        assert oauth.getExpTime().equals(oauth2.getExpTime());

        oathServer.updateInfo(oauth);
        Thread.sleep(10);

        Oauth oauth3 = oathServer.queryInfoByCode("12");

        System.out.println(oauth.getExpTime() + " : " + oauth3.getExpTime());
        assert oauth.getExpTime().equals(oauth3.getExpTime()) == false;
    }
}
