package org.noear.solon.cloud.extend.aws.s3;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.aws.s3.service.CloudFileServiceS3Imp;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(S3Props.instance.getFileAccessKey())) {
            return;
        }

        if (S3Props.instance.getFileEnable()) {
            CloudManager.register(CloudFileServiceS3Imp.getInstance());
        }
    }
}
