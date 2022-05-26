package com.sqltoy;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class Config {
    @Bean
    public DataSource db(@Inject("${datasource}") HikariDataSource ds) {
        return ds;
    }
    @Bean("tow")
    public DataSource db2(@Inject("${datasource_tow}") HikariDataSource ds) {
        return ds;
    }

}
