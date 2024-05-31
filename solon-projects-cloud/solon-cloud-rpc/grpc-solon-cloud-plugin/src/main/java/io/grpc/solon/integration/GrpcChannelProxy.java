package io.grpc.solon.integration;

import io.grpc.*;
import io.grpc.solon.annotation.GrpcClient;
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
    private GrpcClient anno;

    public GrpcChannelProxy(GrpcClient anno) {
        this.anno = anno;
        String name = Utils.annoAlias(anno.value(), anno.name());

        if (Utils.isEmpty(anno.group())) {
            upstream = LoadBalance.get(name);
        } else {
            upstream = LoadBalance.get(anno.group(), name);
        }

        if (upstream == null) {
            throw new IllegalStateException("No service upstream found: " + name);
        }
    }

    private Channel getChannel() {
        String server = upstream.getServer();

        Channel real = channelMap.computeIfAbsent(server, k -> {
            URI uri = URI.create(k);
            ManagedChannelBuilder builder = ManagedChannelBuilder
                    .forAddress(uri.getHost(), uri.getPort())
                    .usePlaintext();

            return builder.build();
        });

        return real;
    }

    @Override
    public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(MethodDescriptor<RequestT, ResponseT> methodDescriptor, CallOptions callOptions) {
        return getChannel().newCall(methodDescriptor, callOptions);
    }

    @Override
    public String authority() {
        return getChannel().authority();
    }
}
