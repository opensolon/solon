package demo;

import org.apache.shardingsphere.solon.ShardingSphereSupplier;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

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
    DataSource shardingSphere(@Inject("${sharding.demo1}") ShardingSphereSupplier supplier) throws Exception {
        return supplier.get();
    }

    @Bean(name = "shardingDs2")
    DataSource shardingSphere2(@Inject("${sharding.demo2}") ShardingSphereSupplier supplier) throws Exception {
        return supplier.get();
    }
}
