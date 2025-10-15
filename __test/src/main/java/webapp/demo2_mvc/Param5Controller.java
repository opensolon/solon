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

import org.noear.snack4.ONode;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import webapp.models.CatType;
import webapp.models.CatTypeModel;

/**
 * @author noear 2020/12/20 created
 */

@Mapping("/demo2/param5")
@Controller
public class Param5Controller {
    @Mapping("test1")
    public String test1(String a, @Param("params[a]") String a2) {
        return a + ":" + a2;
    }

    @Mapping("test2")
    public String test2(CatType cat) {
        return cat.title;
    }

    @Mapping("test3")
    public String test3(CatTypeModel model) {
        return model.getCat().title;
    }

    @Mapping("test4")
    public String test4(Context ctx) {
        return ONode.ofBean(ctx.paramMap().toValuesMap()).toJson();
    }

    @Mapping("test5")
    public String postArguments(String name) {
        return name;
    }
}