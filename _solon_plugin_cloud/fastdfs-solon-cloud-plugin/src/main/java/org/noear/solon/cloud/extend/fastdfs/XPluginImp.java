package org.noear.solon.cloud.extend.fastdfs;

import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.fastdfs.service.CloudFileServiceFastDFSImpl;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author liaocp
 * @since 1.12
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CloudProps cloudProps = new CloudProps(context, "fastdfs");

        if (cloudProps.getFileEnable()) {
            CloudManager.register(new CloudFileServiceFastDFSImpl(context, cloudProps));
        }
    }
}
