/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.data.datasource;

import org.noear.solon.Utils;
import org.noear.solon.core.Props;
import org.noear.solon.core.PropsConverter;
import org.noear.solon.core.util.ClassUtil;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 数据源工具
 *
 * @author noear
 * @since 1.11
 */
public class DsUtils {

    /**
     * 解析类型
     * */
    private static Class<?> resolveType(Properties props){
        //::类型
        String typeStr = props.getProperty("type");
        if (Utils.isEmpty(typeStr)) {
            //缺少类型配置
            throw new IllegalStateException("Missing type configuration");
        }
        props.remove("type");

        Class<?> typeClz = ClassUtil.loadClass(typeStr);
        if (typeClz == null || DataSource.class.isAssignableFrom(typeClz) == false) {
            throw new IllegalStateException("Type configuration not is data source");
        }

        return typeClz;
    }

    private static Class<?> resolveTypeOrDefault(Properties props, Class<?> typeDef){
        String typeStr = props.getProperty("type");
        if (Utils.isEmpty(typeStr)) {
            return typeDef;
        }else {
            props.remove("type");

            Class<?> typeClz = ClassUtil.loadClass(typeStr);
            if (typeClz == null || DataSource.class.isAssignableFrom(typeClz) == false) {
                throw new IllegalStateException("Type configuration not is data source");
            }

            return typeClz;
        }
    }

    public static DataSource buildDs(Properties props) {
        Class<?> typeClz = resolveType(props);

        return  buildDs(props, typeClz);
    }

    public static DataSource buildDs(Properties props, Class<?> typeClz) {
        return  (DataSource) PropsConverter.global().convert(props, typeClz);
    }

    /**
     * 构建数据源字典
     */
    public static Map<String, DataSource> buildDsMap(Properties props) {
        Class<?> typeClz = resolveType(props);

        return buildDsMap(props, typeClz);
    }

    /**
     * 构建数据源字典
     */
    public static Map<String, DataSource> buildDsMap(Properties props, Class<?> typeDef) {
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
            throw new IllegalStateException("Missing data source configuration");
        }


        Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();
        groupProps.forEach((key, prop) -> {
            if (prop.size() > 1) {
                //超过1个以上的，才可能是数据源属性
                Class<?> typeClz = resolveTypeOrDefault(prop, typeDef);
                DataSource source = buildDs(prop, typeClz);
                dataSourceMap.put(key, source);
            }
        });

        return dataSourceMap;
    }
}
