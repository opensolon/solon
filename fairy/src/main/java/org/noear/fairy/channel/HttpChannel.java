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
    public Result call(FairyConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable {
        HttpUtils http = HttpUtils.http(url).headers(headers);

        if (cfg.getDecoder().enctype() == Enctype.application_json) {
            http.header("X-Serialization", ContextTypes.at_type_json);
        } else if (cfg.getDecoder().enctype() == Enctype.application_hessian) {
            http.header("X-Serialization", ContextTypes.at_hession);
        } else if (cfg.getDecoder().enctype() == Enctype.application_protobuf) {
            http.header("X-Serialization", ContextTypes.at_protobuf);
        }

        if(method == null || method.length() == 0){
            method = "POST";
        }

        //1.执行并返回
        Response response = null;

        if (cfg.getEncoder().enctype() == Enctype.form_urlencoded) {
            if (args != null && args.size() > 0) {
                response = http.data(args).exec(method);
            } else {
                response = http.exec("GET");
            }
        }

        if (cfg.getEncoder().enctype() == Enctype.application_json) {
            String json = (String) cfg.getEncoder().encode(args);
            response = http.bodyTxt(json, ContextTypes.ct_json).exec(method);
        }

        if (cfg.getEncoder().enctype() == Enctype.application_hessian) {
            InputStream stream = new ByteArrayInputStream((byte[]) cfg.getEncoder().encode(args));
            response = http.bodyRaw(stream, ContextTypes.ct_hessian).exec(method);
        }

        if (cfg.getEncoder().enctype() == Enctype.application_protobuf) {
            InputStream stream = new ByteArrayInputStream((byte[]) cfg.getEncoder().encode(args));
            response = http.bodyRaw(stream, ContextTypes.ct_protobuf).exec(method);
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
