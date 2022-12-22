package org.noear.solon.cloud.extend.qiniu.kodo;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.qiniu.kodo.service.CloudFileServiceKodoImp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CloudProps cloudProps = new CloudProps(context, "qiniu.kodo");

        if (Utils.isEmpty(cloudProps.getFileAccessKey())) {
            return;
        }

        if (cloudProps.getFileEnable()) {
            CloudManager.register(new CloudFileServiceKodoImp(cloudProps));
        }
    }
}
