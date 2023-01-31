package demo_job.controller;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.core.handle.Context;

import java.util.Date;

/**
 * 云端调度的定时任务（本地实现时，就在本地调试了）
 */
@CloudJob(name = "job2",cron7x = "3s")
public class Job2 implements CloudJobHandler {
    @Override
    public void handle(Context ctx) throws Throwable {
        System.out.println("云端定时任务：job2:" + new Date());
    }
}
