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
package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Path;
import org.noear.solon.annotation.Singleton;

/**
 * @author noear 2022/12/10 created
 */
@Singleton(false)
@Mapping("/demo2/path")
@Controller
public class PathController {
    @Mapping("/test0**")
    public String test0() {
        return "ok";
    }

    @Mapping("/test1/?")
    public String test1() {
        return "ok";
    }

    @Mapping("/test2/?*")
    public String test2() {
        return "ok";
    }

    @Mapping("/test3/{name}")
    public String test3_a(@Path("name") String name) {
        return "ok";
    }

    @Mapping("/test3/b")
    public String test3_b(String name) {
        return name;
    }

    @Mapping("/test4/{id}")
    public Object test4(Object id) {
        return id;
    }
}
