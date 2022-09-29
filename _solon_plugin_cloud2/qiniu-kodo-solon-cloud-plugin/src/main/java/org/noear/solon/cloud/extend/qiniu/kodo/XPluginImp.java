package org.noear.solon.cloud.extend.qiniu.kodo;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
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
        if (Utils.isEmpty(KodoProps.instance.getFileAccessKey())) {
            return;
        }

        if (KodoProps.instance.getFileEnable()) {
            CloudManager.register(new CloudFileServiceKodoImp(KodoProps.instance));
        }
    }
}
