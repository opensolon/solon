package org.noear.solonclient;

import java.util.Map;

public class ChannelHttp implements IChannel {
    public static final ChannelHttp instance = new ChannelHttp();

    @Override
    public String call(XProxy proxy, Map<String, String> headers, Map<String, String> args) throws Exception{
        if(proxy._enctype == Enctype.form_data) {
            return HttpUtils
                    .http(proxy._url)
                    .data(args)
                    .headers(headers)
                    .post();
        }else{
            return HttpUtils
                    .http(proxy._url)
                    .bodyTxt(proxy._serializer.stringify(args),"application/json")
                    .headers(headers)
                    .post();
        }
    }
}
