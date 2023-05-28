package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.shardingds.ShardingDataSource;

import javax.sql.DataSource;

/**
 * ShardingSphere 配置
 *
 * @author sorghum
 * @since 2.3.1
 */
@Configuration
public class Config {
    @Bean(name = "shardingDs1", typed = true)
    public DataSource shardingSphere(@Inject("${sharding.demo1}") ShardingDataSource ds) throws Exception {
        return ds;
    }

    @Bean(name = "shardingDs2")
    public DataSource shardingSphere2(@Inject("${sharding.demo2}") ShardingDataSource ds) throws Exception {
        return ds;
    }
}
