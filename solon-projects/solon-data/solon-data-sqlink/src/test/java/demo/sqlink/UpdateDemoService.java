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
import org.noear.solon.annotation.Managed;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.sqlink.SqLink;

@Managed
public class UpdateDemoService {
    @Inject // or @Inject("main")
    SqLink sqLink;

    // 根据id更新email
    public void updateEmailById(int id, String newEmail) {
        sqLink.update(User.class)
                .set(u -> u.getEmail(), newEmail)
                .where(u -> u.getId() == id)
                .executeRows();
    }

    // 根据id更新name和email
    public void updateNameAndEmailById(int id, String newName, String newEmail) {
        sqLink.update(User.class)
                .set(u -> u.getEmail(), 100)
                .set(u -> u.getUsername(), newName)
                .where(u -> u.getId() == id)
                .executeRows();
    }
}
