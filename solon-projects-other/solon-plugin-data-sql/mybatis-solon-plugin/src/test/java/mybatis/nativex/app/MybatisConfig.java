package mybatis.nativex.app;

import com.zaxxer.hikari.HikariDataSource;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

/**
 * @author songyinyin
 * @since 2023/5/27 11:30
 */
@Configuration
public class MybatisConfig {

    /**
     * 配置数据源
     */
    @Bean(name = "db1", typed = true)
    DataSource datasource(@Inject("${datasource}") HikariDataSource ds) {
        return ds;
    }

}
