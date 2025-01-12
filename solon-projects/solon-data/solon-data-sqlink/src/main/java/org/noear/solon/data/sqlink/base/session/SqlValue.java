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
package org.noear.solon.data.sqlink.base.session;

import org.noear.solon.data.sqlink.base.SqLinkConfig;
import org.noear.solon.data.sqlink.base.intercept.DoNothingInterceptor;
import org.noear.solon.data.sqlink.base.intercept.Interceptor;
import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;
import org.noear.solon.data.sqlink.base.toBean.handler.TypeHandlerManager;
import org.noear.solon.data.sqlink.base.toBean.handler.UnKnowTypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.noear.solon.data.sqlink.core.visitor.ExpressionUtil.cast;

/**
 * SQL参数打包
 *
 * @author kiryu1223
 * @since 3.0
 */
public class SqlValue {
    /**
     * 值
     */
    private final Object value;
    /**
     * 类型处理器
     */
    private final ITypeHandler<?> typeHandler;
    /**
     * 拦截器
     */
    private final Interceptor<?> interceptor;

    public SqlValue(Object value) {
        this(value, value == null ? UnKnowTypeHandler.Instance : TypeHandlerManager.get(value.getClass()), DoNothingInterceptor.Instance);
    }

    public SqlValue(Object value, ITypeHandler<?> typeHandler, Interceptor<?> interceptor) {
        this.value = value;
        this.typeHandler = typeHandler;
        this.interceptor = interceptor;
    }

    /**
     * 设置进sql
     */
    public void preparedStatementSetValue(SqLinkConfig config, PreparedStatement preparedStatement, int index) throws SQLException {
        typeHandler.setValue(preparedStatement, index, cast(interceptor.doIntercept(cast(value), config)));
    }
}
