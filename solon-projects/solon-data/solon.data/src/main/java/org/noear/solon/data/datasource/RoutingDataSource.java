package org.noear.solon.data.datasource;

import javax.sql.DataSource;

/**
 * 可路由数据源
 *
 * @author noear
 * @since 2.8
 */
public interface RoutingDataSource {
    /**
     * 确定当前目标数据源
     */
    DataSource determineCurrentTarget();
}
