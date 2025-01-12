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
package org.noear.solon.data.sql;

import org.noear.solon.data.sql.impl.SimpleSqlUtilsFactory;

/**
 * Sql 配置类
 *
 * @author noear
 * @since 3.0
 */
public class SqlConfiguration {
    private static SqlUtilsFactory factory = new SimpleSqlUtilsFactory();

    /**
     * 获取工厂
     */
    public static SqlUtilsFactory getFactory() {
        return factory;
    }

    /**
     * 设置工厂
     */
    public static void setFactory(SqlUtilsFactory factory) {
        if (factory != null) {
            SqlConfiguration.factory = factory;
        }
    }
}