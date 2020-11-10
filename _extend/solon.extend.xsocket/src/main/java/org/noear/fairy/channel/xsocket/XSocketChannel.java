package org.noear.fairy.channel.xsocket;


import org.noear.fairy.FairyConfig;
import org.noear.fairy.IChannel;
import org.noear.fairy.Result;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;

import java.util.Map;

public class XSocketChannel implements IChannel {
    public XSession session;

    public XSocketChannel(XSession session) {
        this.session = session;
    }

    @Override
    public Result call(FairyConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> body) throws Throwable {

        //0.尝试解码器的过滤
        cfg.getDecoder().filter(cfg, method, url, headers, body);

        XMessage message = null;
        Map<String, Object> data = body;//new HashMap<>();
//        data.put("headers", headers);
//        data.put("body", body);


        //1.执行并返回


        switch (cfg.getEncoder().enctype()) {
            case application_hessian: {
                message = XMessage.wrap(url, (byte[]) cfg.getEncoder().encode(data));
                break;
            }
            case application_protobuf: {
                message = XMessage.wrap(url, (byte[]) cfg.getEncoder().encode(data));
                break;
            }
            default: {
                // 默认：application_json
                //
                String json = (String) cfg.getEncoder().encode(data);
                message = XMessage.wrap(url, json.getBytes());
                break;
            }
        }

        XMessage response = session.sendAndResponse(message);

        if (response == null) {
            return null;
        }

        //2.构建结果
        Result result = new Result(200, response.content());

        //3.返回结果
        return result;
    }
}
