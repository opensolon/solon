package org.noear.solonclient;

import java.util.Map;

public class ChannelHttp implements IChannel {
    public static final ChannelHttp instance = new ChannelHttp();

    private static final String enctype_json = "application/json";

    @Override
    public String call(XProxy proxy, Map<String, String> headers, Map<String, String> args) throws Exception {
        HttpUtils http = HttpUtils.http(proxy._url).headers(headers);

        if (proxy._enctype == Enctype.form_data) {
            if (args != null && args.size() > 0) {
                return http.data(args).post();
            } else {
                return http.get();
            }
        } else {
            return http.bodyTxt(proxy._serializer.stringify(args), enctype_json).post();
        }
    }
}
