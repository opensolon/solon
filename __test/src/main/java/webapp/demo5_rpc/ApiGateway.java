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
package webapp.demo5_rpc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Gateway;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Result;

/**
 * @author noear 2023/6/13 created
 */
@Mapping("demo53/api2/**")
@Controller
public class ApiGateway extends Gateway {
    @Override
    protected void register() {
        add(ApiDemo.class);
    }

    @Override
    public void render(Object obj, Context c) throws Throwable {
        if (c.getRendered()) {
            return;
        } else {
            c.setRendered(true);
        }

        if (obj instanceof ModelAndView) {
            c.result = obj;
        } else {
            if (obj instanceof Result) {
                c.result = obj;
            } else {
                c.result = Result.succeed(obj);
            }
        }

        c.render(c.result);
    }
}
