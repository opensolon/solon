package org.noear.solon.data.datasource;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * 动态数据源
 *
 * @author noear
 * @since 1.11
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    public DynamicDataSource(Properties props) {
        if (props == null || props.size() == 0) {
            //缺少配置
            throw new IllegalStateException("Missing dynamic data source configuration");
        }

        String strictStr = props.getProperty("strict", "false");
        props.remove("strict");

        Map<String, DataSource> dataSourceMap = DynamicDsUtils.buildDsMap(props);

        //::获取默认数据源
        DataSource defSource = dataSourceMap.get("default");

        if (defSource == null) {
            throw new IllegalStateException("Missing default data source configuration");
        }

        //::初始化
        init(defSource, dataSourceMap, Boolean.parseBoolean(strictStr));
    }



    public DynamicDataSource(DataSource defaultTargetDataSource, Map<String, DataSource> targetDataSources) {
        init(defaultTargetDataSource, targetDataSources, false);
    }

    public DynamicDataSource(DataSource defaultTargetDataSource, Map<String, DataSource> targetDataSources, boolean strict) {
        init(defaultTargetDataSource, targetDataSources, strict);
    }


    /**
     * 获取数据源集合
     */
    public Map<String, DataSource> getDataSourceMap() {
        return Collections.unmodifiableMap(targetDataSources);
    }


    @Override
    protected String determineCurrentKey() {
        return DynamicDsUtils.getCurrent();
    }
}