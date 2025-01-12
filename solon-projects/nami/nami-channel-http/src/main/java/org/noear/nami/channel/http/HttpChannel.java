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
package org.noear.nami.channel.http;

import org.noear.nami.*;
import org.noear.nami.common.ContentTypes;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Http 通道
 */
public class HttpChannel extends ChannelBase implements Channel {
    public static final HttpChannel instance = new HttpChannel();

    @Override
    public Result call(Context ctx) throws Throwable {
        pretreatment(ctx);

        //0.检测method
        boolean is_get = ContentTypes.METHOD_GET.equals(ctx.action);
        String url = ctx.url;

        //0.尝试重构url
        // a. GET 方法，统一用 query 参数
        // b. body 提交时，参数转到 query 上（有 NamiBody 的参数 已经从 args 移除了）
        if ((is_get && ctx.args.size() > 0) || (ctx.body != null && ctx.args.size() > 0)) {
            StringBuilder sb = new StringBuilder(ctx.url);
            //如果URL中含有固定参数,应该用'&'添加参数
            sb.append(ctx.url.contains("?") ? "&" : "?");

            for (Map.Entry<String, Object> kv : ctx.args.entrySet()) {
                if (kv.getValue() != null) {
                    sb.append(kv.getKey()).append('=')
                            .append(HttpUtils.urlEncode(kv.getValue().toString()))
                            .append('&');
                }
            }

            url = sb.substring(0, sb.length() - 1);
        }

        if (ctx.config.getDecoder() == null) {
            throw new IllegalArgumentException("There is no suitable decoder");
        }

        //0.尝试解码器的过滤
        ctx.config.getDecoder().pretreatment(ctx);

        //0.开始构建http
        HttpUtils http = HttpUtils.http(url).headers(ctx.headers).timeout(ctx.config.getTimeout());
        HttpResponse response = null;
        Encoder encoder = ctx.config.getEncoder();

        //1.执行并返回
        if (is_get) {
            response = http.exec(ContentTypes.METHOD_GET);
        } else if (ctx.args.size() == 0 && ctx.body == null) {
            // 增强query时，已经将body的参数移除了，所以需要一起判断body也为空
            response = http.exec(ContentTypes.METHOD_GET);
        } else {
            if (encoder == null) {
                String ct0 = ctx.headers.getOrDefault(ContentTypes.HEADER_CONTENT_TYPE, "");

                if (ct0.length() > 0) {
                    encoder = NamiManager.getEncoder(ct0);
                }
            }

            if(encoder != null){
                if(encoder.bodyRequired() && ctx.body == null){
                    throw new NamiException("The encoder requires parameters with '@NamiBody'");
                }
            }

            //有 body 或者有 encoder；则用编码方式
            if (ctx.body != null || encoder != null) {
                if (encoder == null) {
                    encoder = ctx.config.getEncoderOrDefault();
                }

                if (encoder == null) {
                    //有 body 的话，必须要有编译
                    throw new IllegalArgumentException("There is no suitable decoder");
                }

                byte[] bytes = encoder.encode(ctx.bodyOrArgs());

                if (bytes != null) {
                    response = http.body(bytes, encoder.enctype()).exec(ctx.action);
                }
            } else {
                response = http.data(ctx.args).exec(ctx.action);
            }
        }

        if (response == null) {
            return null;
        }

        //2.构建结果
        Result result = new Result(response.code(), response.bodyAsBytes());

        //2.1.设置头
        for (String name : response.headerNames()) {
            for (String v : response.headers(name)) {
                result.headerAdd(name, v);
            }
        }

        //2.2.设置字符码
        Charset contentEncoding = response.contentEncoding();
        if (contentEncoding != null) {
            result.charsetSet(contentEncoding);
        }

        //3.返回结果
        return result;
    }
}