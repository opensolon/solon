package org.noear.solon.cloud.extend.jmdns;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.jmdns.service.CloudDiscoveryServiceJmdnsImpl;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.util.LogUtil;

import java.io.IOException;

/**
 * @author noear
 * @since 1.10
 */
public class XPluginImp implements Plugin {
    CloudDiscoveryServiceJmdnsImpl discoveryServiceJmdnsImpl;

    @Override
    public void start(AppContext context) {
        CloudProps cloudProps = new CloudProps(context, "jmdns");

        if (Utils.isEmpty(cloudProps.getServer())) {
            return;
        }

        if (cloudProps.getDiscoveryEnable()) {
            discoveryServiceJmdnsImpl = new CloudDiscoveryServiceJmdnsImpl(cloudProps);
            CloudManager.register(discoveryServiceJmdnsImpl);
        }
    }

    @Override
    public void stop() throws IOException {
        if (discoveryServiceJmdnsImpl != null) {
            discoveryServiceJmdnsImpl.close();
        }
    }
}
