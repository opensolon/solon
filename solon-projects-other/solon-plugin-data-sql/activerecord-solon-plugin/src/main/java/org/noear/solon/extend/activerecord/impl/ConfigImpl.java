package org.noear.solon.extend.activerecord.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.noear.solon.data.tran.TranUtils;

import com.jfinal.plugin.activerecord.Config;
import com.jfinal.plugin.activerecord.IContainerFactory;
import com.jfinal.plugin.activerecord.cache.ICache;
import com.jfinal.plugin.activerecord.dialect.Dialect;

/**
 * 配置实现（实现与Solon事务对接）
 *
 * @author noear
 * @since 1.4
 */
public class ConfigImpl extends Config {
    public ConfigImpl(String name, DataSource dataSource) {
        super(name, dataSource);
    }

    public ConfigImpl(String name, DataSource dataSource, Dialect dialect) {
        super(name, dataSource, dialect);
    }

    public ConfigImpl(String name, DataSource dataSource, Dialect dialect, boolean showSql, boolean devMode, int transactionLevel, IContainerFactory containerFactory, ICache cache) {
        super(name, dataSource, dialect, showSql, devMode, transactionLevel, containerFactory, cache);
    }

    public ConfigImpl(String name, DataSource dataSource, int transactionLevel) {
        super(name, dataSource, transactionLevel);
    }

    @Override
    public void close(Connection conn) {
        if (TranUtils.inTrans()) {
            return;
        }

        super.close(conn);
    }

    @Override
    public void close(ResultSet rs, Statement st, Connection conn) {
        if (TranUtils.inTrans()) {
            return;
        }

        super.close(rs, st, conn);
    }

    @Override
    public void close(Statement st, Connection conn) {
        if (TranUtils.inTrans()) {
            return;
        }

        super.close(st, conn);
    }

    @Override
    public boolean isInTransaction() {
        return TranUtils.inTrans() || super.isInTransaction();
    }
}