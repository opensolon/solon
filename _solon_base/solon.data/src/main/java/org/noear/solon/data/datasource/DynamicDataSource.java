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
 * @since 1.10
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    public DynamicDataSource(Properties props) {
        if (props == null || props.size() == 0) {
            //缺少配置
            throw new IllegalStateException("DynamicDataSource missing configuration");
        }

        //::类型
        String type = props.getProperty("type");

        if (Utils.isEmpty(type)) {
            //缺少类型配置
            throw new IllegalStateException("DynamicDataSource missing type configuration");
        }
        props.remove("type");
        Class<?> typeClz = Utils.loadClass(type);

        if (DataSource.class.isAssignableFrom(typeClz)) {
            throw new IllegalStateException("DynamicDataSource type not is DataSource");
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
            throw new IllegalStateException("DynamicDataSource missing ds configuration");
        }


        Map<String, DataSource> dataSourceMap = new HashMap<>();
        groupProps.forEach((key, prop) -> {
            DataSource source = (DataSource) PropsConverter.global().convert(prop, typeClz);
            dataSourceMap.put(key, source);
        });


        //::获取默认数据源
        DataSource defSource = dataSourceMap.get("default");


        //::初始化
        initDo(defSource, dataSourceMap);
    }

    public DynamicDataSource(DataSource defaultTargetDataSource, Map<String, DataSource> targetDataSources) {
        initDo(defaultTargetDataSource, targetDataSources);
    }


    /**
     * 获取数据源集合
     * */
    public Map<String, DataSource> getDataSourceMap() {
        return Collections.unmodifiableMap(targetDataSources);
    }


    @Override
    protected String determineCurrentKey() {
        return DynamicDataSourceHolder.get();
    }
}
