package org.noear.nami.channel.http.okhttp;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 1.7
 */
public class HttpClientCached {
    private final static Dispatcher sharedDispatcher;
    private final static Map<Integer, OkHttpClient> cachedClient = new HashMap<>();

    static {
        sharedDispatcher = new Dispatcher();
        sharedDispatcher.setMaxRequests(20000);
        sharedDispatcher.setMaxRequestsPerHost(10000);

        cachedClient.put(10, create(10));
    }

    private static OkHttpClient create(int timeout) {
        return new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .dispatcher(sharedDispatcher)
                .build();
    }


    public static OkHttpClient getClient(int timeout) {
        if (timeout == 0) {
            timeout = 10;
        }

        OkHttpClient tmp = cachedClient.get(timeout);

        if (tmp == null) {
            synchronized (HttpClientCached.class) {
                tmp = cachedClient.get(timeout);

                if (tmp == null) {
                    tmp = create(timeout);
                    cachedClient.put(timeout, tmp);
                }
            }
        }

        return tmp;
    }
}
