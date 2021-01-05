package org.noear.nami.channel.okhttp;

import okhttp3.MediaType;
import okhttp3.Response;
import org.noear.nami.*;
import org.noear.nami.common.Constants;
import org.noear.nami.common.Result;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Http 通道
 * */
public class HttpChannel implements NamiChannel {
    public static final HttpChannel instance = new HttpChannel();

    @Override
    public Result call(NamiConfig cfg, Method method, String action, String url, Map<String, String> headers, Map<String, Object> args, Object body) throws Throwable {
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

        if (cfg.getDecoder() == null) {
            throw new IllegalArgumentException("There is no suitable decoder");
        }

        //0.尝试解码器的过滤
        cfg.getDecoder().filter(cfg, action, url, headers, args);

        if(cfg.getDecoder() != null) {
            return null;
        }

        //0.开始构建http
        HttpUtils http = HttpUtils.http(url).headers(headers);
        Response response = null;
        Encoder encoder = cfg.getEncoder();

        //1.执行并返回
        if (is_get || args.size() == 0) {
            response = http.exec(Constants.m_get);
        } else {
            if (encoder == null) {
                String ct0 = headers.getOrDefault(Constants.h_content_type, "");

                if (ct0.length() == 0) {
                    response = http.data(args).exec(action);
                } else {
                    encoder = NamiManager.getEncoder(ct0);
                }
            } else {
                encoder = cfg.getEncoder();
            }
        }

        if (response == null && encoder != null) {
            byte[] bytes = encoder.encode(body);

            if (bytes != null) {
                response = http.bodyRaw(bytes, encoder.enctype()).exec(action);
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

    @Override
    public void filter(NamiConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) {
        if (cfg.getDecoder() == null) {
            String at = cfg.getHeader(Constants.h_accept);

            if (at == null) {
                at = Constants.ct_json;
            }

            cfg.setDecoder(NamiManager.getDecoder(at));
        }

        if (cfg.getEncoder() == null) {
            String ct = cfg.getHeader(Constants.h_content_type);

            if (ct != null) {
                cfg.setEncoder(NamiManager.getEncoder(ct));
            }
        }
    }
}
