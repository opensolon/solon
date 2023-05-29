package org.noear.solon.socketd;

import org.noear.solon.core.message.Message;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author noear
 * @since 1.6
 */
public class RequestManager {
    /**
     * 请求并响应的默认超时时间（单位：秒）
     * */
    public static int REQUEST_AND_RESPONSE_TIMEOUT_SECONDS = 30;

    /**
     * 请求暂存处
     * */
    private static Map<String, CompletableFuture<Message>> requests = new ConcurrentHashMap<>();

    /**
     * 注册请求
     *
     * @param message 请求消息
     * @param future 回调
     * */
    public static void register(Message message, CompletableFuture<Message> future) {
        requests.putIfAbsent(message.key(), future);
    }

    public static CompletableFuture<Message> get(String key){
        return requests.get(key);
    }

    public static void remove(String key){
        requests.remove(key);
    }
}
