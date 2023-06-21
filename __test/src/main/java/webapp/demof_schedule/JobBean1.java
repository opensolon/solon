package webapp.demof_schedule;

import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.scheduling.annotation.Scheduled;

/**
 * @author noear 2023/3/20 created
 */
//@Component
public class JobBean1 {
    @CloudJob
    public void job1(){
        System.out.println("job1");
    }

    @Scheduled(fixedRate = 1000 * 3)
    public void job2(){
        System.out.println("job1");
    }
}
