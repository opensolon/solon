package org.noear.fairy.channel;

import okhttp3.MediaType;
import okhttp3.Response;
import org.noear.fairy.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * Http 通道
 * */
public class HttpChannel implements IChannel {
    public static final HttpChannel instance = new HttpChannel();

    @Override
    public Result call(FairyConfig cfg, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable {
        HttpUtils http = HttpUtils.http(url).headers(headers);

        if(cfg.getDecoder().enctype() == Enctype.application_json){
            http.header("X-Serialization","@type_json");
        }else if(cfg.getDecoder().enctype() == Enctype.application_hessian){
            http.header("X-Serialization","@hession");
        }

        //1.执行并返回
        Response response = null;

        if (cfg.getEncoder().enctype() == Enctype.form_urlencoded) {
            if (args != null && args.size() > 0) {
                response = http.data(args).exec("POST");
            } else {
                response = http.exec("GET");
            }
        }

        if (cfg.getEncoder().enctype() == Enctype.application_json) {
            String json = (String) cfg.getEncoder().encode(args);
            response = http.bodyTxt(json, ContextTypes.json).exec("POST");
        }

        if (cfg.getEncoder().enctype() == Enctype.application_hessian) {
            InputStream stream = new ByteArrayInputStream((byte[]) cfg.getEncoder().encode(args));
            response = http.bodyRaw(stream, ContextTypes.hessian).exec("POST");
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
