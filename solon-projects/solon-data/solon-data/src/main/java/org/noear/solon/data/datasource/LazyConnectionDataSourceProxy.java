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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 延迟获取连接的数据源代理
 *
 * <p>包装目标数据源，在调用 {@link #getConnection()} 时不会立即从连接池获取物理连接，
 * 而是返回一个 {@link LazyConnectionProxy} 延迟连接代理。真正的物理连接获取会被推迟到
 * 首次创建 Statement（或 PreparedStatement、CallableStatement）时才发生。</p>
 *
 * <p>连接初始化属性（如 auto-commit、事务隔离级别、只读模式）会被暂存在代理对象中，
 * 等到真正获取连接时再一并应用到物理连接上。如果整个事务过程中从未执行过 SQL，
 * 则 commit、rollback、close 调用会被静默忽略，不会占用连接池中的任何连接。</p>
 *
 * <p>这在虚拟线程（Virtual Thread）场景下尤其有价值：大量并发事务可以在事务开启阶段
 * 不占用连接池连接，只在真正需要执行 SQL 时才获取，用完即还，极大提升连接池利用率。</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * // 原始数据源（如 HikariCP）
 * HikariDataSource ds = new HikariDataSource(config);
 * // 用 LazyConnectionDataSourceProxy 包装
 * DataSource lazyDs = new LazyConnectionDataSourceProxy(ds);
 * }</pre>
 *
 * @author noear
 * @since 4.1
 * @see LazyConnectionProxy
 */
public class LazyConnectionDataSourceProxy extends DataSourceWrapper implements DataSource {

    public LazyConnectionDataSourceProxy(DataSource real) {
        super(real);
    }

    /**
     * 返回一个延迟获取物理连接的 Connection 代理。
     * <p>真正的物理连接会在首次创建 Statement 时才从目标数据源获取。</p>
     */
    @Override
    public Connection getConnection() throws SQLException {
        return new LazyConnectionProxy(getReal());
    }

    /**
     * 返回一个延迟获取物理连接的 Connection 代理（带用户名密码）。
     * <p>真正的物理连接会在首次创建 Statement 时才从目标数据源获取。</p>
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new LazyConnectionProxy(getReal(), username, password);
    }
}