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
package demo.sqlink;

import demo.sqlink.model.User;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.sqlink.SqLink;

import java.util.Arrays;

@Component
public class InsertDemoService {
    @Inject // or @Inject("main")
    SqLink sqLink;

    // 插入一条
    public long insert(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return sqLink.insert(user).executeRows();
    }

    // 插入多条
    public long batchInsert() {
        User user1 = new User();
        user1.setUsername("SOLON");
        user1.setPassword("aaa");
        User user2 = new User();
        user2.setUsername("noear");
        user2.setPassword("bbb");
        User user3 = new User();
        user3.setUsername("没有耳朵");
        user3.setPassword("ccc");
        return sqLink.insert(Arrays.asList(user1, user2, user3)).executeRows();
        // or sqLink.insert(user1).insert(user2).insert(user3).executeRows();
    }
}
