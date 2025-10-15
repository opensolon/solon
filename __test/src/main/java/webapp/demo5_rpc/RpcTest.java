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

import org.noear.nami.Nami;
import org.noear.nami.NamiAttachment;
import org.noear.nami.coder.snack4.Snack4Decoder;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.util.HashMap;
import java.util.Map;

@Mapping("/demo5/rpctest/")
@Controller
public class RpcTest implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        Map<String, Object> map = new HashMap<>();

        NamiAttachment.put("user_name", "noear");

        map.put("HttpChannel", httpOf());
        map.put("SocketChannel", socketOf());

        ctx.render(map);
    }

    private Object httpOf() {
        String root = "http://localhost:" + Solon.cfg().serverPort();

        RockApi client = Nami.builder()
                .decoder(Snack4Decoder.instance)
                .upstream(() -> root)
                .create(RockApi.class);

        return client.test1(12);
    }

    private Object socketOf() {
        int _port = 20000 + Solon.cfg().serverPort();

        RockApi client = Nami.builder().upstream(() -> "tcp://localhost:" + _port)
                .decoder(Snack4Decoder.instance)
                .create(RockApi.class);

        return client.test1(12);
    }
}
