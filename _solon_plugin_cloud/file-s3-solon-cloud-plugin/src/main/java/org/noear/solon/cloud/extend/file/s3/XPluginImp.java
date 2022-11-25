package org.noear.solon.cloud.extend.file.s3;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.file.s3.service.CloudFileServiceS3Imp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.10
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) throws Throwable {
        if (Utils.isEmpty(S3Props.instance.getFileAccessKey())) {
            return;
        }

        if (S3Props.instance.getFileEnable()) {
            CloudManager.register(new CloudFileServiceS3Imp(S3Props.instance));
        }
    }
}
