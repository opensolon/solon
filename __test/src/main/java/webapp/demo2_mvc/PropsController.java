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

import org.noear.snack.ONode;
import org.noear.solon.annotation.*;
import webapp.models.UserModel;

import java.util.Map;

/**
 * @author noear 2021/12/3 created
 */
@Mapping("/demo2/props/")
@Controller
public class PropsController {

    @Mapping("/bean")
    public Object bean(UserModel user) {
        return user;
    }

    @Mapping("/bean_map")
    public Object bean_map(@Body Map<String, Object> body) {
        return ONode.stringify(body);
    }
}