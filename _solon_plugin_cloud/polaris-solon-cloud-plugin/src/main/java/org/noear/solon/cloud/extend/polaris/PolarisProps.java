package org.noear.solon.cloud.extend.polaris;

import com.tencent.polaris.factory.ConfigAPIFactory;
import com.tencent.polaris.factory.config.ConfigurationImpl;
import org.noear.solon.Utils;

import java.net.URL;

/**
 * @author noear
 * @since 1.2
 */
public class PolarisProps {


    private static ConfigurationImpl cfgImpl;

    public static ConfigurationImpl getCfgImpl() {
        if (cfgImpl == null) {
            cfgImpl = (ConfigurationImpl) ConfigAPIFactory.defaultConfig();

            URL cfgUri = Utils.getResource("polaris.yml");
            if (cfgUri == null) {
                //如果没有配置文件，把持久化去掉
                cfgImpl.getConsumer().getLocalCache().setPersistEnable(false);
                cfgImpl.getConfigFile().getServerConnector().setPersistEnable(false);
            }
        }

        return cfgImpl;
    }
}
