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
package webapp.demo2_cache;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CacheRemove;
import webapp.models.UserModel;

import java.util.Date;

@Controller
public class CacheController4 {
    @Cache(tags = "test4_user,test4_${.id}", seconds = 600)
    @Mapping("/cache4/cache")
    public UserModel cache() {
        UserModel u = new UserModel();

        u.setId(12);
        u.setDate(new Date());

        return u;
    }

    @CacheRemove(tags = "test4_${id}")
    @Mapping("/cache4/remove")
    public int remove(int id) {
        return id;
    }
}
