package org.noear.solon.cloud.extend.aws.s3;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.aws.s3.service.CloudFileServiceOfS3HttpImpl;
import org.noear.solon.cloud.extend.aws.s3.service.CloudFileServiceOfS3SdkImpl;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    final String AWS_SDK_TAG = "com.amazonaws.services.s3.AmazonS3ClientBuilder";

    @Override
    public void start(AopContext context) {
        if (S3Props.instance.getFileEnable()) {
            if (Utils.isEmpty(S3Props.instance.getFileAccessKey())) {
                return;
            }

            if (Utils.loadClass(AWS_SDK_TAG) == null) {
                CloudManager.register(new CloudFileServiceOfS3HttpImpl(S3Props.instance));
            } else {
                CloudManager.register(new CloudFileServiceOfS3SdkImpl(S3Props.instance));
            }
        }
    }
}
