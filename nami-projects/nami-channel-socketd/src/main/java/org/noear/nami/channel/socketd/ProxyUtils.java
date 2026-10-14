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
package org.noear.nami.channel.socketd;

import org.noear.nami.Decoder;
import org.noear.nami.Encoder;
import org.noear.nami.Nami;
import org.noear.nami.common.ContentTypes;
import org.noear.socketd.transport.client.ClientSession;

import java.util.function.Supplier;

/**
 * 代理工具
 *
 * @author noear
 * @since 1.10
 */
public class ProxyUtils {
    //真正使用的是 session，服务地址只是占个位
    //后端使用的是 path，hostname 只是为符合 nami 的标准
    private static final String VIRTUAL_SERVER = "sd://nami";

    /**
     * 创建接口
     * */
    public static <T> T create(Supplier<ClientSession> sessions, Encoder encoder, Decoder decoder, Class<T> service) {
        return Nami.builder()
                .encoder(encoder)
                .decoder(decoder)
                .headerSet(ContentTypes.HEADER_ACCEPT, ContentTypes.JSON_VALUE) //相当于指定默认解码器 //如果指定不同的编码器，会被盖掉
                .headerSet(ContentTypes.HEADER_CONTENT_TYPE, ContentTypes.JSON_VALUE) //相当于指定默认编码器
                .channel(new SocketdChannel(sessions))
                .upstream(() -> VIRTUAL_SERVER)
                .create(service);
    }
}
