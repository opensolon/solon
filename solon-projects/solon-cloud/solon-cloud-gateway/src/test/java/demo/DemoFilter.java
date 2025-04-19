package demo;

import features.gateway.sys.AppLogFilter;
import org.noear.solon.cloud.gateway.CloudGatewayFilter;
import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExFilterChain;
import org.noear.solon.core.util.DataThrowable;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.rx.Completable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * @author noear 2025/4/19 created
 */
public class DemoFilter implements CloudGatewayFilter {
    static final Logger log = LoggerFactory.getLogger(AppLogFilter.class);

    @Override
    public Completable doFilter(ExContext ctx, ExFilterChain chain) {
        return Completable.create(emitter -> {
            RunUtil.async(()->{
                try {
                    if (doAuth(ctx) == false) {
                        //没通过，结束（这里不能传递异常，有可能 sa-token 有输出了）
                        emitter.onError(new DataThrowable());
                    }
                } catch (Throwable ex) {
                    emitter.onError(ex);
                }
            });
        }).then(() -> chain.doFilter(ctx));
    }

    boolean doAuth(ExContext ctx) throws IOException {
        return true;
    }
}
