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

import org.noear.solon.core.util.RankEntity;
import org.noear.solon.data.sql.bound.RowConverterFactory;
import org.noear.solon.data.sql.impl.DefaultConverter;
import org.noear.solon.data.sql.intercept.SqlExecuteInterceptor;
import org.noear.solon.data.sql.intercept.SqlExecutor;
import org.noear.solon.data.sql.intercept.SqlExecutorWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sql 配置类
 *
 * @author noear
 * @since 3.0
 */
public class SqlConfiguration {
    private static List<RankEntity<SqlExecuteInterceptor>> interceptorList = new ArrayList<>();

    /**
     * 设置执行拦截器
     */
    public static void addInterceptor(SqlExecuteInterceptor interceptor, int index) {
        SqlConfiguration.interceptorList.add(new RankEntity<>(interceptor, index));
    }

    /**
     * 执行拦截
     *
     * @param cmd     命令
     * @param excutor 执行器
     */
    public static Object doIntercept(SqlCommand cmd, SqlExecutor excutor) throws SQLException {
        return new SqlExecutorWrapper(interceptorList, excutor).execute(cmd);
    }


    /// //////////////////////

    private static RowConverterFactory converter = new DefaultConverter();

    /**
     * 获取转换工厂
     */
    public static RowConverterFactory getConverter() {
        return converter;
    }

    /**
     * 设置转换工厂
     */
    public static void setConverter(RowConverterFactory converter) {
        if (converter != null) {
            SqlConfiguration.converter = converter;
        }
    }
}