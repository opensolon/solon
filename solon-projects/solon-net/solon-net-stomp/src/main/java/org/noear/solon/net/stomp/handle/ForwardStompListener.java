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
package org.noear.solon.net.stomp.handle;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ContextHolder;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.net.stomp.*;
import org.noear.solon.net.stomp.broker.impl.StompBrokerMedia;
import org.noear.solon.net.stomp.listener.StompListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Stomp 消息转发监听器
 *
 * @author noear
 * @since 3.0
 */
public class ForwardStompListener implements StompListener {
    static final Logger log = LoggerFactory.getLogger(ForwardStompListener.class);

    private final StompBrokerMedia brokerMedia;

    public ForwardStompListener(StompBrokerMedia brokerMedia) {
        this.brokerMedia = brokerMedia;
    }

    @Override
    public void onFrame(StompSession session, Frame frame) throws Throwable {
        if (Commands.SEND.equals(frame.getCommand())) {
            onSend(session, frame);
        }
    }

    protected void onSend(StompSession session, Frame frame) throws Throwable {
        String destination = frame.getHeader(Headers.DESTINATION);

        if (Utils.isEmpty(destination)) {
            log.warn("This stomp message is missing route, source={}", frame.getSource());

            Frame frame1 = Frame.newBuilder()
                    .command(Commands.ERROR)
                    .payload("Required 'destination' header missed")
                    .build();

            session.send(frame1);
        } else {
            toBroker(session, destination, frame);
            toApp(session, destination, frame);
        }
    }

    /**
     * 转给经理人
     */
    protected void toBroker(StompSession session, String destination, Frame frame) throws Throwable {
        if (brokerMedia.isBrokerDestination(destination)) {
            Message message = new Message(frame.getPayload()).headerAdd(frame.getHeaderAll());
            brokerMedia.emitter.sendTo(destination, message);
        }
    }

    /**
     * 转给应用
     */
    protected void toApp(StompSession session, String destination, Frame frame) throws Throwable {
        StompContext ctx = new StompContext(session, frame, destination, brokerMedia);
        Handler handler = Solon.app().router().matchMain(ctx);

        if (handler != null) {
            try {
                ContextHolder.currentSet(ctx);
                handler.handle(ctx);

                if (ctx.asyncStarted() == false) {
                    ctx.innerCommit();
                }
            } finally {
                ContextHolder.currentRemove();
            }
        }
    }
}