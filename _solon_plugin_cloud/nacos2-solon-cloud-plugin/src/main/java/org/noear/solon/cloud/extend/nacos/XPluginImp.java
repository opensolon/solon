package org.noear.solon.cloud.extend.nacos;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.nacos.service.CloudConfigServiceNacosImp;
import org.noear.solon.cloud.extend.nacos.service.CloudDiscoveryServiceNacosImp;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    private static final String SERVER2 = "solon.cloud.nacos2.server";

    @Override
    public void start(AopContext context) {
        CloudProps cloudProps;
        if (context.cfg().get(SERVER2) == null) {
            cloudProps = new CloudProps(context, "nacos");
        } else {
            //@Deprecated //历史原因出了个2，需要更正
            cloudProps = new CloudProps(context, "nacos2");
        }

        if (Utils.isEmpty(cloudProps.getServer())) {
            return;
        }

        //1.登记配置服务
        if (cloudProps.getConfigEnable()) {
            CloudManager.register(new CloudConfigServiceNacosImp(cloudProps));

            //1.1.加载配置
            CloudClient.configLoad(cloudProps.getConfigLoad());
        }

        //2.登记发现服务
        if (cloudProps.getDiscoveryEnable()) {
            CloudManager.register(new CloudDiscoveryServiceNacosImp(cloudProps));
        }
    }
}
