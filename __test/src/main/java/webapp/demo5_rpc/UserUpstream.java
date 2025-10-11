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

import org.noear.solon.annotation.Component;
import org.noear.solon.core.LoadBalance;

/**
 * 定义一个负载器（可以对接发现服务）
 * */
@Component("local")
public class UserUpstream implements LoadBalance {
    @Override
    public String getServer(int port) {
        return "http://localhost:8080";
    }
}
