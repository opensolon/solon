package org.apache.shardingsphere.solon;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author noear
 * @since 2.2
 */
public class ShardingSphereFactory {
    private Properties properties;

    public ShardingSphereFactory(Properties properties) {
        this.properties = properties;
    }

    public DataSource createDataSource() throws IOException, SQLException {
        String config = properties.getProperty("config.yml");

        if (Utils.isEmpty(config)) {
            String yamlStr = YamlUtil.toYamlString(properties); //new Yaml(dumperOptions).dump(propertiesAsMap);

            return YamlShardingSphereDataSourceFactory.createDataSource(yamlStr.getBytes());
        } else {
            URL url = ResourceUtil.findResource(config);
            return YamlShardingSphereDataSourceFactory.createDataSource(new File(url.getFile()));
        }
    }
}
