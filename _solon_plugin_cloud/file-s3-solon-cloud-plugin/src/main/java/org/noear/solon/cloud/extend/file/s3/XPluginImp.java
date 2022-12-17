package org.noear.solon.cloud.extend.file.s3;

import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.file.s3.service.CloudFileServiceImpl;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author 等風來再離開
 * @since 1.11
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        if (S3Props.instance.getFileEnable()) {
            CloudManager.register(new CloudFileServiceImpl(S3Props.instance));
        }
    }
}
