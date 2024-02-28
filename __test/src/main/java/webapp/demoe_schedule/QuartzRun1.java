package webapp.demoe_schedule;

import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

@Scheduled(fixedRate = 200  ,name = "QuartzRun1")
public class QuartzRun1 implements Runnable {
    @Override
    public void run() {
        System.out.println("我是定时任务: QuartzRun1(200ms) -- " + new Date().toString());
        //throw new RuntimeException("异常");
        System.out.println("如果间隔太长，我可能被配置给控制了");
    }
}
