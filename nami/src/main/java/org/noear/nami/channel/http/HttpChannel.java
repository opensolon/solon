package org.noear.nami.channel.http;

import okhttp3.MediaType;
import okhttp3.Response;
import org.noear.nami.*;
import org.noear.nami.channel.Constants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Http 通道
 * */
public class HttpChannel implements NamiChannel {
    public static final HttpChannel instance = new HttpChannel();

    @Override
    public Result call(NamiConfig cfg, Method method, String action, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable {
        //0.检测method
        boolean is_get = Constants.m_get.equals(action);

        //0.尝试重构url
        if (is_get && args.size() > 0) {
            StringBuilder sb = new StringBuilder(url).append("?");
            args.forEach((k, v) -> {
                if (v != null) {
                    sb.append(k).append("=")
                            .append(HttpUtils.urlEncode(v.toString()))
                            .append("&");
                }
            });

            url = sb.substring(0, sb.length() - 1);
        }

        //0.尝试解码器的过滤
        cfg.getDecoder().filter(cfg, action, url, headers, args);

        //0.开始构建http
        HttpUtils http = HttpUtils.http(url).headers(headers);
        Response response = null;
        Encoder encoder = cfg.getEncoder();

        if (encoder == null) {
            encoder = NamiManager.getEncoder(Constants.ct_form_urlencoded);
        }

        //1.执行并返回
        if (is_get) {
            response = http.exec(Constants.m_get);
        } else {
            String ct0 = headers.getOrDefault(Constants.h_content_type, "");

            if (encoder.enctype().contentType.equals(Constants.ct_form_urlencoded)) {
                if (ct0.length() == 0) {
                    if (args.size() == 0) {
                        //没参数按GET来
                        response = http.exec(Constants.m_get);
                    } else {
                        response = http.data(args).exec(action);
                    }
                } else {
                    encoder = NamiManager.getEncoder(ct0);
                }
            } else {
                encoder = cfg.getEncoder();
            }
        }

        if (response == null && encoder != null) {
            byte[] bytes = encoder.encode(args);
            if (bytes == null) {
                response = http.bodyRaw(bytes, encoder.enctype().contentType).exec(action);
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
