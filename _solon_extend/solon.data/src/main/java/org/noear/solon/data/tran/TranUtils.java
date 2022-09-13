package org.noear.solon.data.tran;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Note;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.core.util.RunnableEx;
import org.noear.solon.data.annotation.TranAnno;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务工具
 *
 * @author noear
 * @since 1.0
 * */
public class TranUtils {
    private static TranExecutor executor = ()->false;
    static {
        Solon.context().getWrapAsyn(TranExecutor.class, bw -> executor = bw.raw());
    }

    /**
     * 执行事务
     * */
    public static void execute(Tran tran, RunnableEx runnable) throws Throwable {
        executor.execute(tran, runnable);
    }

    /**
     * 回滚事务
     * */
    public static void rollback(RunnableEx runnable) {
        rollback(null, runnable);
    }
    /**
     * 回滚事务
     * */
    public static void rollback(Tran tran, RunnableEx runnable) {
        if (tran == null) {
            tran = new TranAnno();
        }

        try {
            execute(tran, () -> {
                runnable.run();
                throw new SQLException();
            });
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            if (e instanceof RollbackException == false) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 是否在事务中
     */
    @Note("是否在事务中")
    public static boolean inTrans() {
        return executor.inTrans();
    }

    /**
     * 是否在事务中且只读
     */
    @Note("是否在事务中且只读")
    public static boolean inTransAndReadOnly() {
        return executor.inTransAndReadOnly();
    }

    /**
     * 获取链接
     */
    @Note("获取链接")
    public static Connection getConnection(DataSource ds) throws SQLException {
        return executor.getConnection(ds);
    }
}
