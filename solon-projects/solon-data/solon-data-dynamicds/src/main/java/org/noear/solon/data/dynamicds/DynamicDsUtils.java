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

import org.noear.solon.data.datasource.DsUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

/**
 * 动态数据源切换
 *
 * @author noear
 * @since 1.11
 * @deprecated 3.0 {@link org.noear.solon.data.datasource.DsUtils}
 */
@Deprecated
public class DynamicDsUtils {
    /**
     * 构建数据源字典
     *
     * @param props 配置
     * @deprecated 3.0 {@link org.noear.solon.data.datasource.DsUtils#buildDsMap(Properties)}
     */
    @Deprecated
    public static Map<String, DataSource> buildDsMap(Properties props) {
        return DsUtils.buildDsMap(props);
    }

    /**
     * 构建数据源字典
     *
     * @param props   配置
     * @param typeClz 数据源类型
     * @deprecated 3.0 {@link org.noear.solon.data.datasource.DsUtils#buildDsMap(Properties, Class<?>)}
     */
    @Deprecated
    public static Map<String, DataSource> buildDsMap(Properties props, Class<?> typeClz) {
        return DsUtils.buildDsMap(props, typeClz);
    }
}