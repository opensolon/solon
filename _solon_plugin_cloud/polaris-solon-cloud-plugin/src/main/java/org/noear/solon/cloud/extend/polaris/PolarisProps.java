package org.noear.solon.cloud.extend.polaris;

import com.tencent.polaris.factory.ConfigAPIFactory;
import com.tencent.polaris.factory.config.ConfigurationImpl;
import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class PolarisProps {
    public static final CloudProps instance = new CloudProps("polaris");

    static ConfigurationImpl cfgImpl;

    public static ConfigurationImpl getCfgImpl() {
        if (cfgImpl == null) {
            cfgImpl = (ConfigurationImpl) ConfigAPIFactory.defaultConfig();
        }

        return cfgImpl;
    }
}
