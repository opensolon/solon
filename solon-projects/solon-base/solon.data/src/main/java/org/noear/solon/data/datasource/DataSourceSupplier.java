package org.noear.solon.data.datasource;

import org.noear.solon.data.cache.CacheFactory;
import org.noear.solon.data.cache.CacheLib;
import org.noear.solon.data.cache.CacheService;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * 数据源提供者（根据配置，自动构建）
 *
 * @author noear
 * @since 1.10
 */
public class DataSourceSupplier implements Supplier<DataSource> {
    private DataSource real;
    private String type;

    public DataSourceSupplier(Properties props) {
        type = props.getProperty("type");
        real = DsUtils.buildDs(props);
    }

    @Override
    public DataSource get() {
        return real;
    }
}
