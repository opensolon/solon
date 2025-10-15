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
package features.socketd;

import org.junit.jupiter.api.Test;
import org.noear.nami.Nami;
import org.noear.nami.channel.socketd.SocketdProxy;
import org.noear.nami.coder.snack4.Snack4Decoder;
import org.noear.nami.coder.snack4.Snack4Encoder;
import org.noear.snack.ONode;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demoh_socketd.HelloRpcService;
import webapp.utils.ContentTypes;

import java.util.HashMap;
import java.util.Map;

@SolonTest(App.class)
public class SocketResponseTest {
    @Test
    public void test() throws Throwable{
        int _port = 8080 + 20000;

        ClientSession session = SocketD.createClient("tcp://localhost:"+_port)
                .listen(SocketdProxy.socketdToHandler)
                .openOrThow();


        String root = "tcp://localhost:" + _port;

        Entity rst = session.sendAndRequest(root + "/demog/中文/1", new StringEntity("Hello 世界!")).await();

        String tmp = rst.dataAsString();
        System.out.println(tmp);

        assert "我收到了：Hello 世界!".equals(tmp);
    }

    @Test
    public void test_rpc_message() throws Throwable {
        int _port = 8080 + 20000;

        ClientSession session = SocketD.createClient("tcp://localhost:"+ _port)
                .listen(SocketdProxy.socketdToHandler)
                .openOrThow();


        String root = "tcp://localhost:" + _port;
        Map<String, Object> map = new HashMap<>();
        map.put("name", "noear");
        String map_josn = ONode.stringify(map);


        Entity rst = session.sendAndRequest(root + "/demoh/rpc/hello",
                new StringEntity(map_josn).metaStringSet(ContentTypes.JSON)).await();
        String rst_str = ONode.deserialize(rst.dataAsString());

        System.out.println("收到:" + rst_str);

        assert "name=noear".equals(rst_str);
    }

    @Test
    public void test_rpc_api() throws Throwable {
        int _port = 8080 + 20000;

        HelloRpcService rpc = Nami.builder()
                .encoder(Snack4Encoder.instance)
                .decoder(Snack4Decoder.instance)
                .upstream(() -> "tcp://localhost:" + _port)
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");

        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

}
