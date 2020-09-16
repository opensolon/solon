package org.noear.solon.extend.beetlsql;

import org.beetl.sql.clazz.kit.BeetlSQLException;
import org.beetl.sql.core.DefaultConnectionSource;
import org.beetl.sql.core.ExecuteContext;
import org.noear.solon.core.XTranUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 链接源（完成与Solon的事务注解对接）
 *
 * @author noear
 * @since 2020-09-01
 * */
public class DbConnectionSource extends DefaultConnectionSource {
    public DbConnectionSource(DataSource master, DataSource[] slaves) {
        super(master, slaves);
    }
    //Override


    @Override
    public Connection getConn(ExecuteContext ctx, boolean isUpdate) {
        //只有一个数据源
        if (this.slaves == null || this.slaves.length == 0) {
            return this.getWriteConn(ctx);
        }
        //如果是更新语句，也得走master
        if (isUpdate) {
            return this.getWriteConn(ctx);
        }

        //在事物里都用master，除了readonly事物
        if (isTransaction()) {
            boolean isReadOnly = XTranUtils.inTransAndReadOnly();
            if (!isReadOnly) {
                return this.getWriteConn(ctx);
            }
        }

        return this.getReadConn(ctx);
    }

    @Override
    protected Connection doGetConnection(DataSource ds) {
        try {
            return XTranUtils.getConnection(ds);
        } catch (SQLException e) {
            throw new BeetlSQLException(BeetlSQLException.CANNOT_GET_CONNECTION, e);
        }
    }

    //Override
    public boolean isTransaction() {
        return XTranUtils.inTrans();
    }

}
