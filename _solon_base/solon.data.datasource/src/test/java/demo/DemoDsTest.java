package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.datasource.DynamicDataSource;

import javax.sql.DataSource;

/**
 * @author noear 2022/11/23 created
 */

public class DemoDsTest {
    @Configuration
    public class ConfigDemo {
        @Bean("db_user")
        public DataSource dsUser(@Inject("$demo.ds.db_user}") DynamicDataSource dataSource) {
            return dataSource;
        }

        @Bean("db_order")
        public DataSource dsOrder(@Inject("$demo.ds.db_order}") DynamicDataSource dataSource) {
            return dataSource;
        }
    }
}