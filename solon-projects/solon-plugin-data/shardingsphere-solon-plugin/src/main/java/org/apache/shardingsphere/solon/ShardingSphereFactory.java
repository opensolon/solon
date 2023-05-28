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
        String fileUri = properties.getProperty("file");
        if (Utils.isNotEmpty(fileUri)) {
            URL url = ResourceUtil.findResource(fileUri);
            return YamlShardingSphereDataSourceFactory.createDataSource(new File(url.getFile()));
        }

        String configTxt = properties.getProperty("config");
        if (Utils.isNotEmpty(configTxt)) {
            return YamlShardingSphereDataSourceFactory.createDataSource(configTxt.getBytes());
        }

        throw new IllegalStateException("Invalid sharding sphere configuration");
    }
}
