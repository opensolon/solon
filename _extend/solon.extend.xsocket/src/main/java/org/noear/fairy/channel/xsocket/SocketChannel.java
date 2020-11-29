package org.noear.fairy.channel.xsocket;


import org.noear.fairy.FairyConfig;
import org.noear.fairy.FairyChannel;
import org.noear.fairy.Result;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

import java.util.Map;
import java.util.function.Supplier;

public class SocketChannel implements FairyChannel {
    public Supplier<Session> sessions;

    public SocketChannel(Supplier<Session> sessions) {
        this.sessions = sessions;
    }

    //public SocketChannel handshake();

    @Override
    public Result call(FairyConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable {

        //0.尝试解码器的过滤
        cfg.getDecoder().filter(cfg, method, url, headers, args);

        Message message = null;
        String message_key = Utils.guid();

        StringBuilder header = new StringBuilder();
        if (headers != null) {
            headers.forEach((k, v) -> {
                header.append(k).append("=").append(v).append(";");
            });
        }

        //1.执行并返回

        switch (cfg.getEncoder().enctype()) {
            case application_hessian: {
                message = Message.wrap(message_key, url, header.toString(), (byte[]) cfg.getEncoder().encode(args));
                break;
            }
            default: {
                // 默认：application_json
                //
                String json = (String) cfg.getEncoder().encode(args);
                message = Message.wrap(message_key, url, header.toString(), json.getBytes());
                break;
            }
        }

        if (Solon.cfg().isFilesMode()) {
            System.out.println(message.toString());
        }

        Message response = sessions.get().sendAndResponse(message);

        if (response == null) {
            return null;
        }

        //2.构建结果
        Result result = new Result(200, response.body());

        //3.返回结果
        return result;
    }
}
