package org.noear.solon.extend.schedule;


import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

/**
 * solon.extend.schedule 相对于 cron4j-solon-plugin 的区别：
 *
 * getInterval 和 getThreads 可动态控制；例，夜间或流量小时弹性变小数值.
 *
 * */
public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.context().beanOnloaded(()->{
            Aop.context().beanForeach((k,v)->{
                if(v.raw() instanceof IJob){
                    JobFactory.register(new JobEntity(k,v.raw()));
                }
            });

            JobFactory.run(JobRunner.global);
        });
    }
}