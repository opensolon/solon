/*
 * Copyright 2017-2024 noear.org and authors
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
package demo.server;

import org.noear.solon.annotation.Component;
import org.noear.solon.net.stomp.SimpleStompListener;
import org.noear.solon.net.websocket.WebSocket;

/**
 * 按需扩展，比是必须
 *
 * @author noear
 * @since 2.4
 */
@Component
public class CustomStompListenerImpl extends SimpleStompListener {

    @Override
    public void onOpen(WebSocket socket) {
        String user = socket.param("user");
        System.out.println("建议放鉴权: " + user);
    }

    //@Override
    //public void onConnect(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //}
    //
    //@Override
    //public void onClose(WebSocket socket) {
    //    System.out.println("按需扩展");
    //}
    //
    //@Override
    //public void onDisconnect(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //}
    //
    //@Override
    //public void onSubscribe(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //
    //}
    //
    //@Override
    //public void onUnsubscribe(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //}
    //
    //@Override
    //public void onSend(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //}
    //
    //@Override
    //public void onAck(WebSocket socket, Message message) {
    //    System.out.println("按需扩展");
    //}
}