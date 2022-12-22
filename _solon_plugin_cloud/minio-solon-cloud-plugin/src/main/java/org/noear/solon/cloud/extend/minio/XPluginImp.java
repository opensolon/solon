package org.noear.solon.cloud.extend.minio;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.minio.service.CloudFileServiceMinioImpl;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CloudProps cloudProps = new CloudProps(context,"minio");

        if (cloudProps.getFileEnable()) {
            if (Utils.isEmpty(cloudProps.getFileAccessKey())) {
                return;
            }

            CloudManager.register(new CloudFileServiceMinioImpl(cloudProps));
        }
    }
}
