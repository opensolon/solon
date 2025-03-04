package org.noear.solon.data.rx.sql.intercept;

import org.noear.solon.data.rx.sql.RxSqlCommand;
import org.noear.solon.lang.Preview;
import org.reactivestreams.Publisher;

/**
 * Sql 执行拦截器
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface RxSqlExecuteInterceptor {
    /**
     * 拦截
     *
     * @param cmd      命令
     * @param executor 执行器
     */
    Publisher doIntercept(RxSqlCommand cmd, RxSqlExecutor executor);
}