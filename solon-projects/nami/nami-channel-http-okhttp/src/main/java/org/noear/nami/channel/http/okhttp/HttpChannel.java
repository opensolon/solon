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
package org.noear.nami.channel.http.okhttp;

import okhttp3.MediaType;
import okhttp3.Response;
import org.noear.nami.*;
import org.noear.nami.common.Constants;

/**
 * Http 通道
 */
public class HttpChannel extends ChannelBase implements Channel {
    public static final HttpChannel instance = new HttpChannel();

    @Override
    public Result call(Context ctx) throws Throwable {
        pretreatment(ctx);

        //0.检测method
        boolean is_get = Constants.METHOD_GET.equals(ctx.action);
        String url = ctx.url;

        //0.尝试重构url
        // a. GET 方法，统一用 query 参数
        // b. body 提交时，参数转到 query 上（有 NamiBody 的参数 已经从 args 移除了）
        if ((is_get && ctx.args.size() > 0) || (ctx.bodyInited && ctx.args.size() > 0)) {
            StringBuilder sb = new StringBuilder(ctx.url);
            //如果URL中含有固定参数,应该用'&'添加参数
            sb.append(ctx.url.contains("?") ? "&" : "?");

            ctx.args.forEach((k, v) -> {
                if (v != null) {
                    sb.append(k).append("=")
                            .append(HttpUtils.urlEncode(v.toString()))
                            .append("&");
                }
            });

            url = sb.substring(0, sb.length() - 1);
        }

        if (ctx.config.getDecoder() == null) {
            throw new IllegalArgumentException("There is no suitable decoder");
        }

        //0.尝试解码器的过滤
        ctx.config.getDecoder().pretreatment(ctx);

        //0.开始构建http
        HttpUtils http = HttpUtils.http(url).headers(ctx.headers).timeout(ctx.config.getTimeout());
        Response response = null;
        Encoder encoder = ctx.config.getEncoder();

        //1.执行并返回
        if (is_get) {
            response = http.exec(Constants.METHOD_GET);
        } else if (ctx.args.size() == 0 && ctx.body == null) {
            // 增强query时，已经将body的参数移除了，所以需要一起判断body也为空
            response = http.exec(ctx.action);
        } else {
            if (encoder == null) {
                String ct0 = ctx.headers.getOrDefault(Constants.HEADER_CONTENT_TYPE, "");

                if (ct0.length() == 0) {
                    response = http.data(ctx.args).exec(ctx.action);
                } else {
                    encoder = NamiManager.getEncoder(ct0);
                }
            } else {
                encoder = ctx.config.getEncoder();
            }
        }

        if (response == null && encoder != null) {
            byte[] bytes = encoder.encode(ctx.body);

            if (bytes != null) {
                response = http.bodyRaw(bytes, encoder.enctype()).exec(ctx.action);
            }
        }

        if (response == null) {
            return null;
        }

        //2.构建结果
        Result result = new Result(response.code(), response.body().bytes());

        //2.1.设置头
        for (int i = 0, len = response.headers().size(); i < len; i++) {
            result.headerAdd(response.headers().name(i), response.headers().value(i));
        }

        //2.2.设置字符码
        MediaType contentType = response.body().contentType();
        if (contentType != null) {
            result.charsetSet(contentType.charset());
        }

        //3.返回结果
        return result;
    }
}
