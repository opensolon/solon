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

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;

@Mapping("/demo2/method0")
@Controller
public class MethodDemoController {
    /**
     * (1)没事儿，这么写就够了
     */

    //经典写法
    @Mapping("demo00")
    public String demo00(String name) {
        return name;
    }

    /**
     * (2)像 REST api，强调 method的
     */

    //经典写法
    @Mapping(path = "demo10", method = MethodType.POST)
    public String demo10(String name) {
        return name;
    }

    //新增写法（新旧，2种都可以玩）
    @Post
    @Mapping("demo11")
    public String demo11(String name) {
        return name;
    }

    //经典写法
    @Mapping(value = "demo20", method = {MethodType.HTTP, MethodType.SOCKET})
    public String demo20(String name) {
        return name;
    }

    //新增写法（新旧，2种都可以玩）
    @Socket
    @Http
    @Mapping("demo21")
    public String demo21(String name) {
        return name;
    }
}
