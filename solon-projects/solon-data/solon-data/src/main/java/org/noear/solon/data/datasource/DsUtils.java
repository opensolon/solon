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
package org.noear.solon.data.datasource;

import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.lang.Nullable;

import javax.sql.DataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * 数据源工具
 *
 * @author noear
 * @since 1.11
 * @since 2.9
 */
public class DsUtils {
    public static final String[] DEFAULT_CLASS_PROP_NAMES = {"@type", "type", "class", "dataSourceClassName"};
    public static final String UNTRANSACTION_PROP_NAME = "untransaction";
    /**
     * 解析类型
     */
    private static Class<?> resolveTypeOrNull(Properties props, String[] typePropNames) {
        //@since 2.9 + dataSourceClassName
        String typeStr = Utils.propertyOr(props, typePropNames);

        if (Utils.isNotEmpty(typeStr)) {
            Utils.propertyRemove(props, typePropNames);

            Class<?> typeClz = ClassUtil.loadClass(typeStr);
            if (typeClz == null || DataSource.class.isAssignableFrom(typeClz) == false) {
                throw new IllegalStateException("The type configuration is not a data source");
            }

            return typeClz;
        } else {
            return null;
        }
    }

    private static Class<?> resolveTypeOrDefault(Properties props, Class<?> typeDef, String[] typePropNames) {
        //@since 2.9 + dataSourceClassName
        String typeStr = Utils.propertyOr(props, typePropNames);

        Class<?> typeClz = null;

        if (Utils.isEmpty(typeStr)) {
            //使用默认
            typeClz = typeDef;
        } else {
            //开始构建
            Utils.propertyRemove(props, typePropNames);

            typeClz = ClassUtil.loadClass(typeStr);
        }

        if (typeClz == null || DataSource.class.isAssignableFrom(typeClz) == false) {
            //如果没有？或类型不对？
            throw new IllegalStateException("The type configuration is not a data source");
        }

        return typeClz;
    }

    /**
     * 属性转换为数据源
     *
     * @param props   属性
     * @param typeClz 数据源类型
     */
    private static DataSource convertDo(Properties props, Class<?> typeClz) {
        DataSource ds = (DataSource) PropsConverter.global().convert(props, typeClz);

        if ("true".equals(props.getProperty(UNTRANSACTION_PROP_NAME))) {
            return new UntransactionDataSource(ds);
        } else {
            return ds;
        }
    }

    /**
     * 构建一个数据源（从属性配置里，获取数据源类型）
     *
     * @param props 属性
     */
    public static DataSource buildDs(Properties props) {
        return buildDs(props, null, DEFAULT_CLASS_PROP_NAMES);
    }

    /**
     * 构建一个数据源
     *
     * @param props 属性
     *              * @param typeDef 类型默认
     */
    public static DataSource buildDs(Properties props, Class<?> typeDef) {
        return buildDs(props, typeDef, DEFAULT_CLASS_PROP_NAMES);
    }

    /**
     * 构建一个数据源（从属性配置里，获取数据源类型）
     *
     * @param props         属性
     * @param typePropNames 类型属性名
     */
    public static DataSource buildDs(Properties props, String[] typePropNames) {
        return buildDs(props, null, typePropNames);
    }

    /**
     * 构建一个数据源（从属性配置里，获取数据源类型）
     *
     * @param props         属性
     * @param typeDef       类型默认
     * @param typePropNames 类型属性名
     * @since 3.0
     */
    public static DataSource buildDs(Properties props, Class<?> typeDef, String[] typePropNames) {
        Class<?> typeClz = resolveTypeOrDefault(props, typeDef, typePropNames);

        return convertDo(props, typeClz);
    }


    /**
     * 构建数据源集合
     */
    public static Map<String, DataSource> buildDsMap(Properties props) {
        return buildDsMap(props, DEFAULT_CLASS_PROP_NAMES);
    }

    /**
     * 构建数据源集合
     *
     * @param typeDef 默认数据源类型
     */
    public static Map<String, DataSource> buildDsMap(Properties props, Class<?> typeDef) {
        return buildDsMap(props, typeDef, DEFAULT_CLASS_PROP_NAMES);
    }

    /**
     * 构建数据源集合
     */
    public static Map<String, DataSource> buildDsMap(Properties props, String[] typePropNames) {
        Class<?> typeClz = resolveTypeOrNull(props, typePropNames);

        return buildDsMap(props, typeClz, typePropNames);
    }

    /**
     * 构建数据源集合
     *
     * @param typeDef 默认数据源类型
     */
    public static Map<String, DataSource> buildDsMap(Properties props, @Nullable Class<?> typeDef, String[] typePropNames) {
        //::检测配置
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

        //::数据源构建
        Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();
        for (Map.Entry<String, Props> kv : groupProps.entrySet()) {
            if (kv.getValue().size() > 1) {
                //超过1个属性以上的，才可能是数据源属性
                Class<?> typeClz = resolveTypeOrDefault(kv.getValue(), typeDef, typePropNames);
                DataSource source = convertDo(kv.getValue(), typeClz);
                dataSourceMap.put(kv.getKey(), source);
            }
        }

        return dataSourceMap;
    }

    /**
     * 观查数据源
     *
     * @param context  上下文
     * @param dsName   数据源名
     * @param consumer 消费者
     */
    public static void observeDs(AppContext context, String dsName, Consumer<BeanWrap> consumer) {
        if (Utils.isEmpty(dsName)) {
            context.getWrapAsync(DataSource.class, (dsBw) -> {
                consumer.accept(dsBw);
            });
        } else {
            context.getWrapAsync(dsName, (dsBw) -> {
                if (dsBw.raw() instanceof DataSource) {
                    consumer.accept(dsBw);
                }
            });
        }
    }
}