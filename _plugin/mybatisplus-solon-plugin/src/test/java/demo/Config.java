package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class Config {
    @Bean("db1")
    public DataSource sqlSessionFactory1(@Inject("db1") DataSource dataSource) throws Exception{
       return dataSource;
    }
}
