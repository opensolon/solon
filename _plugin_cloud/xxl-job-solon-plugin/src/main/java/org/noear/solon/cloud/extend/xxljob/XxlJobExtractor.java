package org.noear.solon.cloud.extend.xxljob;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.handler.impl.MethodJobHandler;
import org.noear.solon.core.BeanExtractor;
import org.noear.solon.core.BeanWrap;

import java.lang.reflect.Method;

/**
 * @author noear
 * @since 1.4
 */
class XxlJobExtractor implements BeanExtractor<XxlJob> {
    @Override
    public void doExtract(BeanWrap bw, Method method, XxlJob anno) {
        String name = anno.value();

        if (name.trim().length() == 0) {
            throw new RuntimeException("xxl-job method-jobhandler name invalid, for[" + bw.clz() + "#" + method.getName() + "] .");
        }
        if (XxlJobExecutor.loadJobHandler(name) != null) {
            throw new RuntimeException("xxl-job jobhandler[" + name + "] naming conflicts.");
        }


        method.setAccessible(true);

        // init and destory
        Method initMethod = null;
        Method destroyMethod = null;

        if (anno.init().trim().length() > 0) {
            try {
                initMethod = bw.clz().getDeclaredMethod(anno.init());
                initMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler initMethod invalid, for[" + bw.clz() + "#" + method.getName() + "] .");
            }
        }
        if (anno.destroy().trim().length() > 0) {
            try {
                destroyMethod = bw.clz().getDeclaredMethod(anno.destroy());
                destroyMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("xxl-job method-jobhandler destroyMethod invalid, for[" + bw.clz() + "#" + method.getName() + "] .");
            }
        }

        // registry jobhandler
        XxlJobExecutor.registJobHandler(name, new MethodJobHandler(bw.raw(), method, initMethod, destroyMethod));
    }
}
