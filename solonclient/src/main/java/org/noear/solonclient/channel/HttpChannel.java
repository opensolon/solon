package org.noear.solonclient.channel;

import okhttp3.MediaType;
import okhttp3.Response;
import org.noear.solonclient.Enctype;
import org.noear.solonclient.IChannel;
import org.noear.solonclient.Result;
import org.noear.solonclient.XProxy;

import java.util.Map;

public class HttpChannel implements IChannel {
    public static final HttpChannel instance = new HttpChannel();

    private static final String enctype_json = "application/json";

    @Override
    public Result call(XProxy proxy, Map<String, String> headers, Map<String, String> args) throws Exception {
        HttpUtils http = HttpUtils.http(proxy.url()).headers(headers);
        Response response;
        if (proxy.enctype() == Enctype.form_data) {
            if (args != null && args.size() > 0) {
                response = http.data(args).exec("POST");
            } else {
                response = http.exec("GET");
            }
        } else {
            response = http.bodyTxt(proxy.serializer().stringify(args), enctype_json).exec("POST");
        }

        Result result = new Result();
        //code
        result.code = response.code();
        for (int i = 0, len = response.headers().size(); i < len; i++) {
            //header
            result.headerAdd(response.headers().name(i), response.headers().value(i));
        }
        //body
        result.body = response.body().bytes();
        MediaType contentType = response.body().contentType();
        if (contentType != null) {
            //charset
            result.charset = contentType.charset();
        }

        return result;
    }
}
