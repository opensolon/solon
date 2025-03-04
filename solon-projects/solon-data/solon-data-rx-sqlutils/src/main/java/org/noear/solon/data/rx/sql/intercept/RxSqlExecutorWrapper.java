package org.noear.solon.data.rx.sql.intercept;

import org.noear.solon.core.util.RankEntity;
import org.noear.solon.data.rx.sql.RxSqlCommand;
import org.reactivestreams.Publisher;

import java.util.List;

/**
 * Sql 执行包装器
 *
 * @author noear
 * @since 3.1
 */
public class RxSqlExecutorWrapper implements RxSqlExecutor {
    private final List<RankEntity<RxSqlExecuteInterceptor>> executeInterceptors;
    private final RxSqlExecutor executor;
    private int index;

    public RxSqlExecutorWrapper(List<RankEntity<RxSqlExecuteInterceptor>> executeInterceptors, RxSqlExecutor executor) {
        this.executeInterceptors = executeInterceptors;
        this.executor = executor;
        this.index = 0;
    }

    @Override
    public Publisher execute(RxSqlCommand cmd) {
        if (executor == null) {
            return executeInterceptors.get(index++).target.doIntercept(cmd, this);
        } else {
            if (index < executeInterceptors.size()) {
                return executeInterceptors.get(index++).target.doIntercept(cmd, this);
            } else {
                return executor.execute(cmd);
            }
        }
    }
}