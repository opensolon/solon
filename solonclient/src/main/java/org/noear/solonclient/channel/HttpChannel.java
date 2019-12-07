package org.noear.solonclient.channel;

import org.noear.solonclient.Enctype;
import org.noear.solonclient.IChannel;
import org.noear.solonclient.XProxy;

import java.util.Map;

public class HttpChannel implements IChannel {
    public static final HttpChannel instance = new HttpChannel();

    private static final String enctype_json = "application/json";

    @Override
    public String call(XProxy proxy, Map<String, String> headers, Map<String, String> args) throws Exception {
        HttpUtils http = HttpUtils.http(proxy.url()).headers(headers);

        if (proxy.enctype() == Enctype.form_data) {
            if (args != null && args.size() > 0) {
                return http.data(args).post();
            } else {
                return http.get();
            }
        } else {
            return http.bodyTxt(proxy.serializer().stringify(args), enctype_json).post();
        }
    }
}
