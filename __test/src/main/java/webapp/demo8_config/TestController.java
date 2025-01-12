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
package webapp.demo8_config;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import webapp.models.UserModel;

import java.util.Hashtable;

@Controller
public class TestController {
    @Inject
    private ConfigDemo config;

    @Inject("${classpath:user.yml}")
    private UserModel user;

    @Mapping("/demo8/config_inject")
    public void test(Context c) throws Throwable{
        c.render(config);
    }

    @Mapping("/demo8/config_all")
    public void test2(Context c) throws Throwable{
        c.render(Solon.cfg());
    }

    @Mapping("/demo8/config_system")
    public void test3(Context c) throws Throwable{
        Hashtable hashtable = new Hashtable();
        hashtable.putAll(System.getProperties());

        c.render(hashtable);
    }

    @Mapping("/demo8/user")
    public void user(Context c) throws Throwable{
        c.render(user);
    }
}
