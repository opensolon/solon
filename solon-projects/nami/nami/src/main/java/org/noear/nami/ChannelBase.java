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
package org.noear.nami;

import org.noear.nami.common.ContentTypes;

/**
 * 处理通道基类
 *
 * @author noear
 * @since 2.4
 */
public abstract class ChannelBase implements Channel {
    /**
     * 预处理
     *
     * @param ctx 上下文
     */
    protected void pretreatment(Context ctx) {
        if (ctx.config.getDecoder() == null) {
            String at = ctx.config.getHeader(ContentTypes.HEADER_ACCEPT);

            if (at == null) {
                at = ContentTypes.JSON_VALUE;
            }

            ctx.config.setDecoder(NamiManager.getDecoder(at));

            if (ctx.config.getDecoder() == null) {
                ctx.config.setDecoder(NamiManager.getDecoderFirst());
            }
        }

        if (ctx.config.getEncoder() == null) {
            String ct = ctx.config.getHeader(ContentTypes.HEADER_CONTENT_TYPE);

            if (ct != null) {
                ctx.config.setEncoder(NamiManager.getEncoder(ct));
            }

            //encoder 不再强制初始化
        }
    }
}