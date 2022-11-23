package org.noear.solon.data.datasource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源
 *
 * @author noear
 * @since 1.10
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    public DynamicDataSource(DataSource defaultTargetDataSource, Map<String, DataSource> targetDataSources) {
        super(defaultTargetDataSource, targetDataSources);
    }

    @Override
    protected String determineCurrentKey() {
        return DynamicDataSourceHolder.get();
    }
}
