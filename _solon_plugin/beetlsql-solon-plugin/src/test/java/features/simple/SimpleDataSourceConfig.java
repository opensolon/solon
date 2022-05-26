package features.simple;


import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class SimpleDataSourceConfig {

    @Bean
    public DataSource datasource(@Inject("${db1}") HikariDataSource ds) {
        return ds;
    }
}