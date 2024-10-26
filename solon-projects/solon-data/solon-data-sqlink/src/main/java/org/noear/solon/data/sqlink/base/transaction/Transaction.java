package org.noear.solon.data.sqlink.base.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public interface Transaction extends AutoCloseable
{
    void commit();

    void rollback();

    @Override
    void close();

    Connection getConnection() throws SQLException;

    Integer getIsolationLevel();
}
