package org.noear.solon.extend.nacos;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.Plugin;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.extend.nacos.service.CloudConfigServiceImp;
import org.noear.solon.extend.nacos.service.CloudRegisterServiceImp;

/**
 * @author noear 2021/1/9 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isNotEmpty(CloudProps.getConfigServer())) {
            CloudManager.register(new CloudConfigServiceImp());
        }

        if (Utils.isNotEmpty(CloudProps.getDiscoveryServer())) {
            CloudManager.register(new CloudRegisterServiceImp());
        }
    }
}
