package org.noear.solon.cloud.extend.xxljob.impl;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.impl.MethodJobHandler;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.cloud.extend.xxljob.service.CloudJobServiceImpl;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Action;

import java.lang.reflect.Method;

/**
 * @author noear 2021/5/25 created
 */
public class CloudJobExtractor implements BeanExtractor<CloudJob> {
    @Override
    public void doExtract(BeanWrap bw, Method method, CloudJob anno) {
        String name = anno.name();

        if (name.trim().length() == 0) {
            throw new RuntimeException("xxl-job method-jobhandler name invalid, for[" + bw.clz() + "#" + method.getName() + "] .");
        }
        if (XxlJobExecutor.loadJobHandler(name) != null) {
            throw new RuntimeException("xxl-job jobhandler[" + name + "] naming conflicts.");
        }

        method.setAccessible(true);

        Action action = new Action(bw,  method);

        // registry jobhandler
        CloudJobServiceImpl.instance.register(name, action);
    }
}
