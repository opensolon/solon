package org.noear.solon.cloud.extend.aws.s3;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.aws.s3.service.CloudFileServiceS3OfHttpImp;
import org.noear.solon.cloud.extend.aws.s3.service.CloudFileServiceS3OfSdkImp;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    final String AWS_SDK_TAG = "com.amazonaws.services.s3.AmazonS3ClientBuilder";
    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(S3Props.instance.getFileAccessKey())) {
            return;
        }

        if (S3Props.instance.getFileEnable()) {
            if (Utils.loadClass(AWS_SDK_TAG) == null) {
                CloudManager.register(CloudFileServiceS3OfHttpImp.getInstance());
            } else {
                CloudManager.register(CloudFileServiceS3OfSdkImp.getInstance());
            }
        }
    }
}
