package org.noear.solon.cloud.extend.aliyun.oss;

import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudManager;
import org.noear.solon.cloud.extend.aliyun.oss.service.CloudFileServiceImpl;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/4/7 created
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (Utils.isEmpty(OssProps.instance.getFileBucket())) {
            return;
        }

        if (OssProps.instance.getFileEnable()) {
            CloudManager.register(new CloudFileServiceImpl());
        }
    }
}
