package org.noear.solon.data.rx.sql.intercept;

import org.noear.solon.core.util.RankEntity;
import org.noear.solon.data.rx.sql.RxSqlCommand;
import org.reactivestreams.Publisher;

import java.util.List;

/**
 * Sql 命令调用者
 *
 * @author noear
 * @since 3.1
 */
public class RxSqlCommandInvocation {
    private final List<RankEntity<RxSqlCommandInterceptor>> executeInterceptors;
    private final RxSqlCallable callable;
    private final RxSqlCommand command;

    private int index;

    public RxSqlCommandInvocation(RxSqlCommand command, List<RankEntity<RxSqlCommandInterceptor>> executeInterceptors, RxSqlCallable callable) {
        this.executeInterceptors = executeInterceptors;
        this.callable = callable;
        this.command = command;

        this.index = 0;
    }

    /**
     * 获取命令
     */
    public RxSqlCommand getCommand() {
        return command;
    }

    /**
     * 调用
     */
    public Publisher invoke() {
        if (index < executeInterceptors.size()) {
            return executeInterceptors.get(index++).target.doIntercept(this);
        } else {
            return callable.call();
        }
    }
}