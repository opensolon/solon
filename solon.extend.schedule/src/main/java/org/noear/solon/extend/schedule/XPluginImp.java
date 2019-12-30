package org.noear.solon.extend.schedule;


import org.noear.solon.XApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    @Override
    public void start(XApp app) {
        Aop.beanOnloaded(()->{
            Aop.beanForeach((k,v)->{
                if(v.raw() instanceof IJob){
                    JobFactory.register(new JobEntity(k,v.raw()));
                }
            });

            JobFactory.run(JobRunner.global);
        });
    }
}