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
package demo.boot.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import org.noear.solon.Solon;

/**
 * @author noear 2023/3/23 created
 */
public class Http2 {
    public static void main(String[] args) {
        Solon.start(Http2.class, args, app -> {
            app.onEvent(Undertow.Builder.class, e -> {
                //启用 http2
                e.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
                //再加个 http 监听（server.port 被 https 占了）//如果不需要，则不加
                e.addHttpListener(8081, "0.0.0.0");
            });
        });
    }
}
