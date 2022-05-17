package org.noear.solon.cloud.extend.minio;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.minio.service.CloudFileServiceMinioImp;
import org.noear.solon.core.Plugin;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (Utils.isEmpty(MinioProps.INSTANCE.getFileAccessKey())) {
            return;
        }

        if (MinioProps.INSTANCE.getFileEnable()) {
            CloudManager.register(new CloudFileServiceMinioImp(MinioProps.INSTANCE));
        }
    }
}
