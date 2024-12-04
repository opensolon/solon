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
package org.noear.nami.channel.socketd;

import org.noear.nami.*;
import org.noear.nami.common.ContentTypes;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.entity.EntityDefault;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Socketd 通道
 *
 * @author noear
 * @since 1.2
 * @since 2.6
 */
public class SocketdChannel extends ChannelBase implements Channel {
    public Supplier<ClientSession> sessions;

    public SocketdChannel(Supplier<ClientSession> sessions) {
        this.sessions = sessions;
    }

    /**
     * 调用
     *
     * @param ctx 上下文
     * @return 调用结果
     */
    @Override
    public Result call(Context ctx) throws Throwable {
        pretreatment(ctx);

        if (ctx.config.getDecoder() == null) {
            throw new IllegalArgumentException("There is no suitable decoder");
        }

        //0.尝试解码器的过滤
        ctx.config.getDecoder().pretreatment(ctx);

        //1.确定编码器
        Encoder encoder = ctx.config.getEncoderOrDefault();
        if (encoder == null) {
            encoder = NamiManager.getEncoder(ContentTypes.JSON_VALUE);
        }

        if (encoder == null) {
            throw new IllegalArgumentException("There is no suitable encoder");
        } else {
            if (encoder.bodyRequired() && ctx.body == null) {
                throw new NamiException("The encoder requires parameters with '@NamiBody'");
            }
        }

        //2.构建消息
        ctx.headers.put(ContentTypes.HEADER_CONTENT_TYPE, encoder.enctype());
        byte[] bytes = encoder.encode(ctx.bodyOrArgs());
        Entity request = new EntityDefault().metaMapPut(ctx.headers).dataSet(bytes);

        //3.获取会话
        ClientSession session = sessions.get();

        //4.发送消息
        Entity response = session.sendAndRequest(ctx.url, request, ctx.config.getTimeout() * 1000)
                .await();

        if (response == null) {
            return null;
        }

        //2.构建结果
        Result result = new Result(200, response.dataAsBytes());

        //2.1.设置头
        Map<String, String> map = response.metaMap();
        map.forEach((k, v) -> {
            result.headerAdd(k, v);
        });

        //3.返回结果
        return result;
    }
}
