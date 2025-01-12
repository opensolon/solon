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
import org.noear.solon.Solon;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demoh_socketd.HelloRpcService;

@SolonTest(App.class)
public class SocketRpcTest {

    @Test
    public void test_rpc_api() throws Throwable {
        int _port = 8080 + 20000;

        HelloRpcService rpc = SocketdProxy.create("tcp://localhost:" + _port, HelloRpcService.class);

        String rst = rpc.hello("noear");

        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api2() throws Throwable {
        HelloRpcService rpc = Nami.builder()
                .url("tcp://localhost:" + (Solon.cfg().serverPort() + 20000) + "/demoh/rpc")
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");
        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api3() throws Throwable {
        HelloRpcService rpc = Nami.builder()
                .upstream(() -> "tcp://localhost:" + (Solon.cfg().serverPort() + 20000))
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");
        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api_http1() throws Throwable {
        HelloRpcService rpc = Nami.builder()
                .upstream(() -> "http://localhost:" + Solon.cfg().serverPort())
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");
        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api_ws1() throws Throwable {
        HelloRpcService rpc = Nami.builder()
                .upstream(() -> "sd:ws://localhost:" + (Solon.cfg().serverPort() + 20002))
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");
        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api_ws1_self() throws Throwable {
        HelloRpcService rpc = Nami.builder()
                .upstream(() -> "sd:ws://localhost:" + (Solon.cfg().serverPort()))
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");
        System.out.println(rst);

        assert "name=noear".equals(rst);
    }
}
