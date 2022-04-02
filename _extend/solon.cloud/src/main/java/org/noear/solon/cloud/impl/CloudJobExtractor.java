package org.noear.solon.cloud.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.cloud.model.JobWrapper;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Action;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.4
 */
public class CloudJobExtractor implements BeanExtractor<CloudJob> {
    public static final CloudJobExtractor instance = new CloudJobExtractor();

    @Override
    public void doExtract(BeanWrap bw, Method method, CloudJob anno) {
        if (CloudClient.job() == null) {
            throw new IllegalArgumentException("Missing CloudJobService component");
        }

        //支持${xxx}配置
        String name = Solon.cfg().getByParse(anno.value());
        if(Utils.isEmpty(name)){
            name = Solon.cfg().getByParse(anno.name());
        }
        //支持${xxx}配置
        String description = Solon.cfg().getByParse(anno.description());

        if (name.trim().length() == 0) {
            throw new RuntimeException("CloudJob name invalid, for[" + bw.clz() + "#" + method.getName() + "] .");
        }
        if (CloudClient.job().isRegistered(name)) {
            throw new RuntimeException("CloudJob[" + name + "] naming conflicts.");
        }

        //method,必须返回为void
        Action action = new Action(bw, method);

        CloudClient.job().register(name, anno.cron7x(), description, new JobWrapper(action));
    }
}
