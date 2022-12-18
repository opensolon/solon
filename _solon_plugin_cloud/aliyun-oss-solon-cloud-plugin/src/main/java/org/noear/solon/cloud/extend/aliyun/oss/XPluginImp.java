package org.noear.solon.cloud.extend.aliyun.oss;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
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
        if (OssProps.instance.getFileEnable()) {
            if (Utils.isEmpty(OssProps.instance.getFileAccessKey())) {
                return;
            }

            CloudManager.register(new CloudFileServiceOssImpl(OssProps.instance));
        }
    }
}
