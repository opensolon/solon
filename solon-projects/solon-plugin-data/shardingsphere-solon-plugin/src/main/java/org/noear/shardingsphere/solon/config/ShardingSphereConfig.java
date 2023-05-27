package org.noear.shardingsphere.solon.config;

import com.mchange.io.FileUtils;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.util.ResourceUtil;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * ShardingSphere配置
 *
 * @author sorghum
 * @since 2023/05/26
 */
@Configuration
public class ShardingSphereConfig {

    @Bean(name = "shardingDs", typed = true)
    @Condition(onMissingBean = DataSource.class)
    DataSource shardingSphere(@Inject("${sharding-sphere.config.yml:classpath:sharding.yml}")String yamlUrl) throws IOException, SQLException {
        String yamlStr = null;
        // 如果是classpath:开头的，去掉classpath:，然后从资源文件中获取
        if (yamlUrl.startsWith("classpath:")) {
            yamlUrl = yamlUrl.replace("classpath:", "");
            yamlStr = ResourceUtil.getResourceAsString(yamlUrl);
        }
        // 其次当作文件路径，从文件中获取
        if (yamlStr == null) {
            yamlStr = FileUtils.getContentsAsString(new File(yamlUrl));
        }
        return YamlShardingSphereDataSourceFactory.createDataSource(yamlStr.getBytes());
    }
}
