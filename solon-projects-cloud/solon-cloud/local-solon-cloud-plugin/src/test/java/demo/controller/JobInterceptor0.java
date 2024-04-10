package demo.controller;

import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.CloudJobInterceptor;
import org.noear.solon.cloud.model.Job;

@Component
public class JobInterceptor0 implements CloudJobInterceptor {
    @Override
    public void doIntercept(Job job, CloudJobHandler handler) throws Throwable {
        System.out.println("000");
        handler.handle(job.getContext());
    }
}
