package org.noear.solon.data.datasource;

import org.noear.solon.Utils;
import org.noear.solon.core.Props;
import org.noear.solon.core.PropsConverter;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
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

        //::类型
        String typeStr = props.getProperty("type");
        String strictStr = props.getProperty("strict", "false");

        if (Utils.isEmpty(typeStr)) {
            //缺少类型配置
            throw new IllegalStateException("Missing type configuration");
        }
        props.remove("type");
        props.remove("strict");

        Class<?> typeClz = Utils.loadClass(typeStr);

        if (typeClz == null || DataSource.class.isAssignableFrom(typeClz) == false) {
            throw new IllegalStateException("Type configuration not is data source");
        }


        //::数据源构建
        Props rootProps;
        if (props instanceof Props) {
            rootProps = ((Props) props);
        } else {
            rootProps = new Props();
            rootProps.putAll(props);
        }

        Map<String, Props> groupProps = rootProps.getGroupedProp("");

        if (groupProps.size() == 0) {
            //缺少数据源配置
            throw new IllegalStateException("Missing dynamic data source configuration");
        }


        Map<String, DataSource> dataSourceMap = new HashMap<>();
        groupProps.forEach((key, prop) -> {
            if (prop.size() > 1) {
                //超过1个以上的，才可能是数据源属性
                DataSource source = (DataSource) PropsConverter.global().convert(prop, typeClz);
                dataSourceMap.put(key, source);
            }
        });


        //::获取默认数据源
        DataSource defSource = dataSourceMap.get("default");

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