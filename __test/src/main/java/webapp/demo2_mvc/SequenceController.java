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
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2024/9/25 created
 */
@Mapping("/demo2/sequence")
@Controller
public class SequenceController {
    @Mapping("header")
    public String header(Context ctx) {
        return ctx.header("v1") + ";" + ctx.headerMap().holder("v1").getLastValue();
    }

    @Mapping("cookie")
    public String cookie(Context ctx) {
        return ctx.cookie("v1") + ";" + ctx.cookieMap().holder("v1").getLastValue();
    }

    @Mapping("param")
    public String param(Context ctx) {
        return ctx.param("v1") + ";" + ctx.paramMap().holder("v1").getLastValue();
    }
}
