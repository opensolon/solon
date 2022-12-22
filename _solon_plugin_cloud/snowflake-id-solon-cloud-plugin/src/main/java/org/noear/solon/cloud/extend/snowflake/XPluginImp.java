package org.noear.solon.cloud.extend.snowflake;

import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
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
        CloudProps cloudProps = new CloudProps(context,"snowflake");

        if (cloudProps.getIdEnable()) {
            CloudManager.register(new CloudIdServiceFactoryImp(cloudProps.getIdStart()));
        }
    }
}
