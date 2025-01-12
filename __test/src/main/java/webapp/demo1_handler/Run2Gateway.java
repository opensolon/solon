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
package webapp.demo1_handler;

import org.noear.solon.core.handle.Gateway;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Controller;

/**
 * 简单的http处理(带简单的内部导航 + 前后置处理)
 * */
@Mapping("/demo1/run2/*")
@Controller
public class Run2Gateway extends Gateway {
    @Override
    protected void register() {
        //filter((x,c)->{...;o.doFilter(x)});

        add("send", (c)->{c.output(c.url());});
        add("test", (c)->{c.output(c.url());});
        add("dock", (c)->{c.output(c.url());});
        add("ip", (c)->{c.output(c.remoteIp());});

        //filter((x,c)->{o.doFilter(x);...});
    }
}
