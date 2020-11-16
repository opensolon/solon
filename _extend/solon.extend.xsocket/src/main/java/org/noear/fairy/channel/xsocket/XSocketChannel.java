package org.noear.fairy.channel.xsocket;


import org.noear.fairy.FairyConfig;
import org.noear.fairy.IChannel;
import org.noear.fairy.Result;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageSession;

import java.util.Map;

public class XSocketChannel implements IChannel {
    public MessageSession session;

    public XSocketChannel(MessageSession session) {
        this.session = session;
    }

    @Override
    public Result call(FairyConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable {

        //0.尝试解码器的过滤
        cfg.getDecoder().filter(cfg, method, url, headers, args);

        Message message = null;
        String message_key = Utils.guid();

        //1.执行并返回

        switch (cfg.getEncoder().enctype()) {
            case application_hessian: {
                message = Message.wrap(message_key, url, (byte[]) cfg.getEncoder().encode(args));
                break;
            }
            case application_protobuf: {
                message = Message.wrap(message_key, url, (byte[]) cfg.getEncoder().encode(args));
                break;
            }
            default: {
                // 默认：application_json
                //
                String json = (String) cfg.getEncoder().encode(args);
                message = Message.wrap(message_key, url, json.getBytes());
                break;
            }
        }

        if (Solon.cfg().isFilesMode()) {
            System.out.println(message.toString());
        }

        Message response = session.sendAndResponse(message);

        if (response == null) {
            return null;
        }

        //2.构建结果
        Result result = new Result(200, response.content());

        //3.返回结果
        return result;
    }
}
