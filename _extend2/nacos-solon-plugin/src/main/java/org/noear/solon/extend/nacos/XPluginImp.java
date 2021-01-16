package org.noear.solon.extend.nacos;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.extend.cloud.CloudManager;
import org.noear.solon.extend.nacos.service.CloudConfigServiceImp;
import org.noear.solon.extend.nacos.service.CloudRegisterServiceImp;

/**
 * @author noear 2021/1/9 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        CloudManager.register(new CloudConfigServiceImp());
        CloudManager.register(new CloudRegisterServiceImp());
    }
}
