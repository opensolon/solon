package io.grpc.solon.integration;

import io.grpc.*;
import org.noear.solon.Utils;
import org.noear.solon.core.LoadBalance;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通道代理（提供集群调用支持）
 *
 * @author noear
 * @since 1.9
 */
public class GrpcChannelProxy extends Channel {
    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private LoadBalance upstream;

    public GrpcChannelProxy(String group, String service) {
        if (Utils.isEmpty(group)) {
            upstream = LoadBalance.get(service);
        } else {
            upstream = LoadBalance.get(group, service);
        }

        if (upstream == null) {
            throw new IllegalStateException("No service upstream found: " + service);
        }
    }

    @Override
    public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
        return getChannel().newCall(methodDescriptor, callOptions);
    }

    @Override
    public String authority() {
        return getChannel().authority();
    }

    private Channel getChannel() {
        String server = upstream.getServer();

        Channel real = channelMap.computeIfAbsent(server, k -> {
            URI uri = URI.create(server);
            return ManagedChannelBuilder.forAddress(uri.getHost(), uri.getPort())
                    .usePlaintext()
                    .build();
        });

        return real;
    }
}
