package org.noear.solon.data.rx.sql.intercept;

import org.noear.solon.data.rx.sql.RxSqlCommand;
import org.noear.solon.lang.Preview;
import org.reactivestreams.Publisher;

/**
 * Sql 执行器
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface RxSqlExecutor {
    /**
     * 执行
     *
     * @param cmd 命令
     */
    Publisher execute(RxSqlCommand cmd);
}