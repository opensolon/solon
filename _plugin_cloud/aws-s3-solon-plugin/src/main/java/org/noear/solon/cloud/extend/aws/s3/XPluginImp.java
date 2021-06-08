package org.noear.solon.cloud.extend.aws.s3;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.aws.s3.service.CloudFileServiceS3Imp;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/4/7 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (S3Props.instance.getFileEnable() == false) {
            return;
        }

        if (Utils.isEmpty(S3Props.instance.getFileAccessKey())) {
            return;
        }

        CloudManager.register(CloudFileServiceS3Imp.getInstance());
    }
}
