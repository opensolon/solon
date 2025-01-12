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
package webapp.demof_websocket;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.entity.FileEntity;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.impl.ConfigDefault;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.StrUtils;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.socketd.ToSocketdWebSocketListener;

import java.io.File;

/**
 * @author noear 2024/5/25 created
 */
@ServerEndpoint("/demof/sd")
public class ToSokcetdService extends ToSocketdWebSocketListener {

    public ToSokcetdService() {
        super(new ConfigDefault(false), buildListener());
    }

    /**
     * 构建监听器
     */
    private static Listener buildListener() {
        return new EventListener()
                .doOnOpen(s -> {
                    s.handshake().outMeta("test","1");
                    System.out.println("onOpen: " + s.sessionId() + ", meta=" + s.handshake().paramMap());
                }).doOnMessage((s, m) -> {
                    System.out.println("onMessage: " + m);
                }).doOn("/demo", (s, m) -> {
                    if (m.isRequest()) {
                        s.reply(m, new StringEntity("me to! ref:" + m.dataAsString()));
                    }

                    if (m.isSubscribe()) {
                        int size = m.rangeSize();
                        for (int i = 1; i <= size; i++) {
                            s.reply(m, new StringEntity("me to-" + i));
                        }
                        s.replyEnd(m, new StringEntity("welcome to my home!"));
                    }
                }).doOn("/upload", (s, m) -> {
                    if (m.isRequest()) {
                        String fileName = m.meta(EntityMetas.META_DATA_DISPOSITION_FILENAME);
                        if (StrUtils.isEmpty(fileName)) {
                            s.reply(m, new StringEntity("no file! size: " + m.dataSize()));
                        } else {
                            s.reply(m, new StringEntity("file received: " + fileName + ", size: " + m.dataSize()));
                        }
                    }
                }).doOn("/download", (s, m) -> {
                    if (m.isRequest()) {
                        FileEntity fileEntity = new FileEntity(new File("/Users/noear/Movies/snack3-rce-poc.mov"));
                        s.reply(m, fileEntity);
                    }
                }).doOn("/push", (s, m) -> {
                    if (s.attrHas("push")) {
                        return;
                    }

                    s.attrPut("push", "1");

                    while (true) {
                        if (s.attrHas("push") == false) {
                            break;
                        }

                        s.send("/push", new StringEntity("push test"));
                        RunUtils.runAndTry(() -> Thread.sleep(200));
                    }
                }).doOn("/unpush", (s, m) -> {
                    s.attrMap().remove("push");
                })
                .doOnClose(s -> {
                    System.out.println("onClose: " + s.sessionId());
                }).doOnError((s, err) -> {
                    System.out.println("onError: " + s.sessionId());
                    err.printStackTrace();
                });
    }
}
