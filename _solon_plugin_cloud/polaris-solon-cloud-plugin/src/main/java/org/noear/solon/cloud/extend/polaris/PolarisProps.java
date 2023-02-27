package org.noear.solon.cloud.extend.polaris;

import com.tencent.polaris.factory.ConfigAPIFactory;
import com.tencent.polaris.factory.config.ConfigurationImpl;
import org.noear.solon.Utils;
import org.noear.solon.core.util.ResourceUtil;

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

            URL cfgUri = ResourceUtil.getResource("polaris.yml");
            if (cfgUri == null) {
                //如果没有配置文件，把持久化去掉
                cfgImpl.getConsumer().getLocalCache().setPersistEnable(false);
                cfgImpl.getConfigFile().getServerConnector().setPersistEnable(false);
            }
        }

        return cfgImpl;
    }
}
