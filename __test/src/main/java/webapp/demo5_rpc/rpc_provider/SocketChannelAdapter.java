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
package webapp.demo5_rpc.rpc_provider;

import org.noear.snack.ONode;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;

import java.util.Map;

public class SocketChannelAdapter implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        if (MethodType.SOCKET.name.equals(ctx.method())) {
            String json = ctx.body();
            Map<String, Object> tmp = (Map<String, Object>) ONode.load(json).toData();

            for (Map.Entry<String, Object> kv : tmp.entrySet()) {
                if (kv.getValue() != null) {
                    ctx.paramMap().add(kv.getKey(), kv.getValue().toString());
                }
            }
        }
    }
}
