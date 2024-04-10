package demo.controller;

import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.CloudJobInterceptor;
import org.noear.solon.cloud.model.Job;

@Component(index = 1)
public class JobInterceptor1 implements CloudJobInterceptor {
    @Override
    public void doIntercept(Job job, CloudJobHandler handler) throws Throwable {
        System.out.println("111");
        handler.handle(job.getContext());
    }
}
