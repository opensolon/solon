package org.noear.nami.channel;

import okhttp3.MediaType;
import okhttp3.Response;
import org.noear.nami.NamiChannel;
import org.noear.nami.NamiConfig;
import org.noear.nami.Result;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Http 通道
 * */
public class OkHttpChannel implements NamiChannel {
    public static final OkHttpChannel instance = new OkHttpChannel();

    @Override
    public Result call(NamiConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable {
        //0.检测method
        boolean is_get = Constants.m_get.equals(method);

        //0.尝试重构url
        if (is_get && args.size() > 0) {
            StringBuilder sb = new StringBuilder(url).append("?");
            args.forEach((k, v) -> {
                if (v != null) {
                    sb.append(k).append("=")
                            .append(OkHttpUtils.urlEncode(v.toString()))
                            .append("&");
                }
            });

            url = sb.substring(0, sb.length() - 1);
        }

        //0.尝试解码器的过滤
        cfg.getDecoder().filter(cfg, method, url, headers, args);

        //0.开始构建http
        OkHttpUtils http = OkHttpUtils.http(url).headers(headers);
        Response response = null;


        //1.执行并返回
        if (is_get) {
            response = http.exec(Constants.m_get);
        } else {

            switch (cfg.getEncoder().enctype()) {
                case application_json: {
                    String json = (String) cfg.getEncoder().encode(args);
                    response = http.bodyTxt(json, Constants.ct_json).exec(method);
                    break;
                }
                case application_hessian: {
                    InputStream stream = new ByteArrayInputStream((byte[]) cfg.getEncoder().encode(args));
                    response = http.bodyRaw(stream, Constants.ct_hessian).exec(method);
                    break;
                }
                default: {
                    if (args.size() == 0) {
                        //没参数按GET来
                        response = http.exec(Constants.m_get);
                    } else {
                        response = http.data(args).exec(method);
                    }
                }
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
