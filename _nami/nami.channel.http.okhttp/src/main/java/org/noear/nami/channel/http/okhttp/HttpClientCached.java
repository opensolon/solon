package org.noear.nami.channel.http.okhttp;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 */
public class HttpClientCached {
    private final static Dispatcher dispatcher;
    private final static Map<Integer, OkHttpClient> clientMap = new HashMap<>();

    static {
        dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(20000);
        dispatcher.setMaxRequestsPerHost(10000);

        clientMap.put(10, create(10));
    }

    private static OkHttpClient create(int timeout) {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .build();
    }


    public static OkHttpClient getClient(int timeout) {
        if (timeout == 0) {
            timeout = 10;
        }

        OkHttpClient tmp = clientMap.get(timeout);

        if (tmp == null) {
            synchronized (HttpClientCached.class) {
                tmp = clientMap.get(timeout);

                if (tmp == null) {
                    tmp = create(timeout);
                    clientMap.put(timeout, tmp);
                }
            }
        }

        return tmp;
    }
}
