package demo;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class Config {
    @Bean(value = "db1")
    public DataSource sqlSessionFactory1(@Inject("${test.db1}") HikariDataSource dataSource) throws Exception{
       return dataSource;
    }
}
