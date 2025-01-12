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
package webapp.demoh_socketd;

import org.noear.nami.channel.socketd.SocketdProxy;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

@Mapping(value = "/demoh/rpc", method = MethodType.ALL)
@Remoting
public class HelloRpcServiceImpl implements HelloRpcService {

    public String hello(String name) {
        Context ctx = Context.current();

        if(MethodType.SOCKET.name.equals(ctx.method())) {
            NameRpcService rpc = SocketdProxy.create(ctx, NameRpcService.class);
            name = rpc.name(name);
        }

        return "name=" + name;
    }
}
