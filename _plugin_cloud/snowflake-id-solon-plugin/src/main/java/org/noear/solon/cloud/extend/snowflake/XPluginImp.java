package org.noear.solon.cloud.extend.snowflake;

import org.noear.solon.SolonApp;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.snowflake.service.CloudIdServiceFactoryImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (SnowflakeProps.instance.getIdEnable()) {
            CloudManager.register(new CloudIdServiceFactoryImp(SnowflakeProps.instance.getIdStart()));
        }
    }
}
