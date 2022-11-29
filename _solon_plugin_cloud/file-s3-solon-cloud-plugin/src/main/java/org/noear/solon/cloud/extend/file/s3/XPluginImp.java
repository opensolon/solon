package org.noear.solon.cloud.extend.file.s3;


import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.file.s3.service.CloudFileServiceS3Imp;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
/**
 * @author noear
 * @since 1.11
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CloudManager.register(new CloudFileServiceS3Imp());
    }
}
