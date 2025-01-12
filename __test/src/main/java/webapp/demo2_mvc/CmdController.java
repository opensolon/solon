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

import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Controller;
import org.noear.solon.core.handle.Context;

@Singleton(false)
@Controller
public class CmdController {

    @Inject
    CmdService cmdService;

    @Mapping("/demo2/CMD/{cmd_name}")
    protected void cmd(Context ctx, String cmd_name) throws Exception{
        ctx.sessionRemove("_test_key");

        switch (cmd_name) {
            case "A.0.1": ctx.output(cmdService.name(cmd_name));break;
            case "A.0.2": ctx.output(cmdService.name(cmd_name));break;
            case "A.0.3": ctx.output(cmdService.name(cmd_name));break;
            case "err": throw new RuntimeException("出错");
            default:ctx.status(404);break;
        }
    }
}
