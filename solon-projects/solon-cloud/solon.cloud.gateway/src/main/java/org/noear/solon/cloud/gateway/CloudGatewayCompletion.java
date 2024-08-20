package org.noear.solon.cloud.gateway;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.util.KeyValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 响应式完成器
 *
 * @author noear
 * @since 2.9
 */
public class CloudGatewayCompletion implements CompletableObserver {
    static final Logger log = LoggerFactory.getLogger(CloudGatewayCompletion.class);

    private final ExContext ctx;
    private final HttpServerRequest rawRequest;

    public CloudGatewayCompletion(ExContext ctx, HttpServerRequest rawRequest) {
        this.ctx = ctx;
        this.rawRequest = rawRequest;
    }


    @Override
    public void onSubscribe(@NonNull Disposable d) {
        if (d != null) {
            d.dispose();
        }
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
    public void postComplete() {
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
