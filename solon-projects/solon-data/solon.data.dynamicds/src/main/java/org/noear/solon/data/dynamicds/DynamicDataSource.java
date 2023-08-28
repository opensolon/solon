package org.noear.solon.data.dynamicds;

import org.noear.solon.Utils;
import org.noear.solon.data.datasource.AbstractRoutingDataSource;
import org.noear.solon.data.datasource.DsUtils;
import org.noear.solon.lang.Nullable;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 动态数据源
 *
 * @author noear
 * @since 1.11
 */
public class DynamicDataSource extends AbstractRoutingDataSource implements DataSource {
    public DynamicDataSource() {

    }

    public DynamicDataSource(Properties props) {
        if (props == null || props.size() == 0) {
            //缺少配置
            throw new IllegalStateException("Missing dynamic data source configuration");
        }

        String strictStr = props.getProperty("strict", "false");
        props.remove("strict");
        String defaultStr = props.getProperty("default", "default");
        props.remove("default");

        Map<String, DataSource> dataSourceMap = DsUtils.buildDsMap(props);

        //::获取默认数据源
        DataSource defSource = dataSourceMap.get(defaultStr);

        if (defSource == null) {
            defSource = dataSourceMap.get("master");
        }

        if (defSource == null) {
            throw new IllegalStateException("Missing default data source configuration");
        }

        //::初始化
        setStrict(Boolean.parseBoolean(strictStr));
        setTargetDataSources(dataSourceMap);
        setDefaultTargetDataSource(defSource);
    }

    /**
     * 添加目标数据源
     *
     * @since 2.4
     */
    public void addTargetDataSource(String name, DataSource dataSource) {
        if (targetDataSources == null) {
            targetDataSources = new LinkedHashMap<>();
        }

        targetDataSources.put(name, dataSource);
    }

    /**
     * 添加目标数据源集合
     *
     * @since 2.4
     */
    public void addTargetDataSourceAll(Map<String, DataSource> targetDataSources) {
        if (Utils.isEmpty(targetDataSources)) {
            throw new IllegalArgumentException("Property 'targetDataSources' is required");
        }

        if (targetDataSources == null) {
            targetDataSources = new LinkedHashMap<>();
        }

        targetDataSources.putAll(targetDataSources);
    }

    /**
     * 移除数据源
     */
    public void removeTargetDataSource(String name) throws IOException {
        if (targetDataSources == null) {
            return;
        }

        DataSource ds = targetDataSources.remove(name);
        if (ds != null) {
            closeDataSource(ds);
        }
    }

    /**
     * 获取数据源
     */
    public @Nullable DataSource getTargetDataSource(String name) {
        return targetDataSources.get(name);
    }

    /**
     * 获取数据源
     */
    public DataSource getDefaultTargetDataSource() {
        return defaultTargetDataSource;
    }

    @Override
    protected String determineCurrentKey() {
        return DynamicDsHolder.get();
    }
}