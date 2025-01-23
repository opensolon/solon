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

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * @author noear
 * @since 3.0
 */
public class R2dbcConnectionFactory implements DataSource {
    private final String r2dbcUrl;

    public R2dbcConnectionFactory(String r2dbcUrl) {
        this.r2dbcUrl = r2dbcUrl;
    }

    public ConnectionFactory build() {
        return ConnectionFactories.get(r2dbcUrl);
    }

    public void register(AppContext context, String name, boolean typed) {
        BeanWrap dsBw = context.wrap(name, build(), typed);

        //按名字注册
        context.putWrap(name, dsBw);
        if (typed) {
            //按类型注册
            context.putWrap(ConnectionFactory.class, dsBw);
        }
        //对外发布
        context.beanPublish(dsBw);

        //aot注册
        context.aot().registerEntityType(dsBw.rawClz(), null);
    }

    /////////////////////

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}