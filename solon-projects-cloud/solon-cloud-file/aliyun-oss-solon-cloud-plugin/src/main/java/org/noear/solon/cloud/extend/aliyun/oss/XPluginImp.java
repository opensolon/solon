package org.noear.solon.cloud.extend.aliyun.oss;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.aliyun.oss.service.CloudFileServiceOssImpl;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.3
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        CloudProps cloudProps = new CloudProps(context,"aliyun.oss");

        if (cloudProps.getFileEnable()) {
            if (Utils.isEmpty(cloudProps.getFileAccessKey())) {
                return;
            }

            CloudManager.register(new CloudFileServiceOssImpl(cloudProps));
        }
    }
}
