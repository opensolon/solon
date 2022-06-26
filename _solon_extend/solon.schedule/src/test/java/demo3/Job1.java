package demo3;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.schedule.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2022/6/26 created
 */
@Slf4j
@Scheduled(cron = "* * * * * ? *")
public class Job1 implements Runnable {
    @Override
    public void run() {
        log.info("{}", new Date());
    }
}
