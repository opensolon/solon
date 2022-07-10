package demo;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.solon.integration.SaTokenPathFilter;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.FilterChain;

import java.io.IOException;

/**
 * @author noear 2022/7/10 created
 */
public class SaTokenPathFilterEx extends SaTokenPathFilter {
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        try {
            // 执行全局过滤器
            SaRouter.match(includeList).notMatch(excludeList).check(r -> {
                beforeAuth.run(null);
                auth.run(null);
            });

        } catch (StopMatchException e) {

        } catch (Throwable e) {
            if(e instanceof IOException){
                throw e;
            }

            // 1. 获取异常处理策略结果
            String result = (e instanceof BackResultException) ? e.getMessage() : String.valueOf(error.run(e));

            // 2. 写入输出流
            ctx.contentType("text/plain; charset=utf-8");
            ctx.output(result);
            return;
        }

        // 执行
        chain.doFilter(ctx);
    }
}
