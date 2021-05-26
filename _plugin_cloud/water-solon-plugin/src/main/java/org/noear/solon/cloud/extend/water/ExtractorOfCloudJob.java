package org.noear.solon.cloud.extend.water;

import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Action;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.4
 */
class ExtractorOfCloudJob implements BeanExtractor<CloudJob> {
    @Override
    public void doExtract(BeanWrap bw, Method method, CloudJob anno) {
        String name = anno.value();

        if (name.trim().length() == 0) {
            throw new RuntimeException("CloudJob name invalid, for[" + bw.clz() + "#" + method.getName() + "] .");
        }
        if (CloudClient.job().isRegistered(name)) {
            throw new RuntimeException("CloudJob[" + name + "] naming conflicts.");
        }

        Action action = new Action(bw, method);

        CloudClient.job().register(name, action);
    }
}
