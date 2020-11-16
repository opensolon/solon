package webapp;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class Config {
    /**
     * 使用 xml 配置创建
     */
    @Bean("db1")
    public DataSource db1( @Inject("${test.db1}") HikariDataSource ds) {
        return ds;
    }
}
