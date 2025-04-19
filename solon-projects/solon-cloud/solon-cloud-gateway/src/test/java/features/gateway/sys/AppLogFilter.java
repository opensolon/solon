package features.gateway.sys;

import io.vertx.core.http.HttpClientResponse;
import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.gateway.CloudGatewayFilter;
import org.noear.solon.cloud.gateway.exchange.ExBody;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.cloud.gateway.exchange.impl.ExBodyOfBuffer;
import org.noear.solon.cloud.gateway.exchange.impl.ExBodyOfStream;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.rx.Completable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear 2025/3/29 created
 */
@Component
public class AppLogFilter implements CloudGatewayFilter {
    static final Logger log = LoggerFactory.getLogger(AppLogFilter.class);

    @Override
    public Completable doFilter(ExContext ctx, ExFilterChain chain) {
        return Completable.create(emitter -> {
            //转异步前暂停流接收（如果鉴权没通过，都不用收了）
            ctx.pause();
            //测试同步转异步
            RunUtil.delay(() -> {
                try {
                    //记录请求日志
                    reqLog(ctx);
                    emitter.onComplete();
                } catch (Throwable ex) {
                    emitter.onError(ex);
                }
            }, 1000);
        }).then(() -> chain.doFilter(ctx).doOnComplete(() -> {
            //正确响应，记录日志
            respLog(ctx);
        }).doOnError(err -> {
            //接收响应出错
            log.warn("resp - err:{}", err);
        }));
    }

    private void reqLog(ExContext ctx) {
        log.info("req - method:{}", ctx.rawMethod());
        log.info("req - headers:{}", ctx.rawHeaders());
        log.info("req - query:{}", ctx.rawQueryString());

        ctx.rawBody().andThen(asyncResult -> {
            if (asyncResult.succeeded()) {
                String bodyStr = asyncResult.result().toString();
                log.info("req - body:{}", bodyStr);
            } else {
                log.warn("req - body:err:{}", asyncResult.cause());
            }
        });
    }

    private void respLog(ExContext ctx) {
        log.info("resp - headers:{}", ctx.newResponse().getHeaders());
        ExBody exBody = ctx.newResponse().getBody();

        if (exBody instanceof ExBodyOfStream) {
            //这是原始的 body 转发
            ExBodyOfStream streamBody = (ExBodyOfStream) exBody;
            ((HttpClientResponse) streamBody.getStream()).body().andThen(asyncResult -> {
                if (asyncResult.succeeded()) {
                    String bodyStr = asyncResult.result().toString();
                    log.info("resp - body:{}", bodyStr);
                } else {
                    log.warn("resp - body:err:{}", asyncResult.cause());
                }
            });
        } else if (exBody instanceof ExBodyOfBuffer) {
            //body 是可以被修改的（如果你未修改，这块可以去掉）
            ExBodyOfBuffer bufferBody = (ExBodyOfBuffer) exBody;
            String bodyStr = bufferBody.getBuffer().toString();

            log.info("resp - body:{}", bodyStr);
        }
    }
}
