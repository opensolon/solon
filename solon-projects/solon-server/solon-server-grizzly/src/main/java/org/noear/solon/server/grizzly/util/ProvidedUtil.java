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
package org.noear.solon.server.grizzly.util;

import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http2.Http2AddOn;
import org.glassfish.grizzly.websockets.WebSocketAddOn;
import org.glassfish.grizzly.websockets.WebSocketEngine;
import org.noear.solon.server.grizzly.websocket.GyWebSocketApplication;

/**
 *
 * @author noear
 * @since 3.6
 */
public class ProvidedUtil {

    public static void doAddWsApp(){
        WebSocketEngine.getEngine().register("/", "/*", new GyWebSocketApplication());
    }

    public static void doAddOnH2(NetworkListener listener){
        listener.registerAddOn(new Http2AddOn());
    }

    public static void onAddOnWs(NetworkListener listener){
        listener.registerAddOn(new WebSocketAddOn());
    }
}
