package features.dynamic;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(value = "ds1", typed =true)
    public DataSource ds1(@Inject("${db1}") HikariDataSource ds) {
        return ds;
    }

    @Bean("ds2")
    public DataSource ds2(@Inject("${db1}") HikariDataSource ds) {
        return ds;
    }
}
