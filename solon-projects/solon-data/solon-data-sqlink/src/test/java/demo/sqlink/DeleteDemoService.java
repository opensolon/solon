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

@Component
public class DeleteDemoService {
    @Inject // or @Inject("main")
    SqLink sqLink;

    // 根据id删除
    public long deleteById(int id) {
        return sqLink.delete(User.class)
                .where(u -> u.getId() == id)
                .executeRows();
    }

    // 根据name删除
    public long deleteByName(String name) {
        return sqLink.delete(User.class)
                .where(u -> u.getUsername() == name)
                .executeRows();
    }

    // 根据id和name删除
    public long deleteByName(int id, String name) {
        return sqLink.delete(User.class)
                .where(u -> u.getId() == id && u.getUsername() == name)
                .executeRows();
    }

    // 删除所有错误的邮箱
    public long deleteByBadEmail() {
        return sqLink.delete(User.class)
                // NOT (email LIKE CONCAT('%','@','%'))
                .where(u -> !u.getEmail().contains("@"))
                .executeRows();
    }
}
