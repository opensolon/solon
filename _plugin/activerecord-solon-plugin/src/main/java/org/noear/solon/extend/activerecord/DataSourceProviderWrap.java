package org.noear.solon.extend.activerecord;

import com.jfinal.plugin.activerecord.IDataSourceProvider;

import javax.sql.DataSource;

/**
 * @author noear
 * @since 1.4
 */
class DataSourceProviderWrap implements IDataSourceProvider {
    DataSource ds;

    public DataSourceProviderWrap(DataSource ds) {
        //
        // 为了集成 Solon 事务
        //
        this.ds = new DataSourceProxy(ds);
    }

    @Override
    public DataSource getDataSource() {
        return ds;
    }
}
