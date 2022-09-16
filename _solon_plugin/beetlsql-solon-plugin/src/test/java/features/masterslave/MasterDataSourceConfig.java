package features.masterslave;


import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class MasterDataSourceConfig {

    @Bean(attrs = "slaves=slaveDs1,slaveDs2")
    public DataSource master(@Inject("${db1}") HikariDataSource ds) {
        return ds;
    }

    @Bean("slaveDs1")
    public DataSource slaveDs1(@Inject("${db1}") HikariDataSource ds) {
        return ds;
    }

    @Bean("slaveDs2")
    public DataSource slaveDs2(@Inject("${db1}") HikariDataSource ds) {
        return ds;
    }
}

