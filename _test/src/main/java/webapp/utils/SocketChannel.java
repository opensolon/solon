package webapp.utils;


import org.noear.fairy.FairyConfig;
import org.noear.fairy.FairyException;
import org.noear.fairy.IChannel;
import org.noear.fairy.Result;

import java.util.Map;

/**
 * Socket 通道
 * */
public class SocketChannel implements IChannel {
    public static final SocketChannel instance = new SocketChannel();

    @Override
    public Result call(FairyConfig cfg, String method, String url, Map<String, String> headers, Map<String, Object> args) throws Throwable {
        byte[] message;
        switch (cfg.getEncoder().enctype()) {
            case application_json: {
                String json = (String) cfg.getEncoder().encode(args);
                message = json.getBytes("utf-8");
                break;
            }
            case application_hessian: {
                message = (byte[]) cfg.getEncoder().encode(args);
                break;
            }
            case application_protobuf: {
                message = (byte[]) cfg.getEncoder().encode(args);
                break;
            }
            default: {
                throw new FairyException("SocketChannel not support: " + cfg.getEncoder().enctype().name());
            }
        }

        synchronized (url.intern()) {
            SocketMessage msg = SocketUtils.send(url, message);

            return new Result(msg.charset, msg.content);
        }
    }
}
