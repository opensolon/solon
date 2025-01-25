/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    /**
     * 设置当前数据源名
     * */
    public void setCurrentKey(String name){
        DynamicDsKey.use(name);
    }

    @Override
    public String determineCurrentKey() {
        return DynamicDsKey.current();
    }
}