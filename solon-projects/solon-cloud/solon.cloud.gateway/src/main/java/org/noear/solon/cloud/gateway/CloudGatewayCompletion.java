package org.noear.solon.cloud.gateway;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.util.KeyValues;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 响应式完成器
 *
 * @author noear
 * @since 2.9
 */
public class CloudGatewayCompletion implements Subscriber {
    static final Logger log = LoggerFactory.getLogger(CloudGatewayCompletion.class);

    private final ExContext ctx;
    private final HttpServerRequest rawRequest;

    public CloudGatewayCompletion(ExContext ctx, HttpServerRequest rawRequest) {
        this.ctx = ctx;
        this.rawRequest = rawRequest;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Object o) {
        //不管
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            if (throwable instanceof StatusException) {
                StatusException status = (StatusException) throwable;
                ctx.newResponse().status(status.getCode());

                if (status.getCode() == 404) {
                    return;
                }
            } else {
                ctx.newResponse().status(500);
            }

            log.error(throwable.getMessage(), throwable);
        } finally {
            postComplete();
        }

    }

    @Override
    public void onComplete() {
        postComplete();
    }

    /**
     * 提交异步完成
     */
    protected void postComplete() {
        HttpServerResponse rawResponse = rawRequest.response();

        rawResponse.setStatusCode(ctx.newResponse().getStatus());

        for (KeyValues<String> kv : ctx.newResponse().getHeaders().values()) {
            rawResponse.putHeader(kv.getKey(), kv.getValues());
        }

        if (ctx.newResponse().getBody() != null) {
            rawResponse.end(ctx.newResponse().getBody());
        } else {
            rawResponse.end();
        }
    }
}
