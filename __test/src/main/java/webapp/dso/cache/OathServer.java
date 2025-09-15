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
package webapp.dso.cache;

import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.annotation.Managed;

import java.time.LocalDateTime;

/**
 * @author noear 2022/1/15 created
 */
@Managed
public class OathServer {
    @Cache(key = "oath_test_#{code}", seconds = 2592000)
    public Oauth queryInfoByCode(String code) {
        Oauth oauth = new Oauth();
        oauth.setCode(code);
        oauth.setExpTime(LocalDateTime.now());

        return oauth;
    }


    @CacheRemove(keys = "oath_test_#{result.code}")
    public Oauth updateInfo2(Oauth oauth) {
        return oauth;
    }

    @CacheRemove(keys = "oath_test_#{oauth.code}")
    public void updateInfo(Oauth oauth) {

    }
}
