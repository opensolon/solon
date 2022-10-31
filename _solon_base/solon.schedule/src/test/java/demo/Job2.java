package demo;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.schedule.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2021/12/28 created
 */
@Slf4j
@Scheduled(cron = "0/10 * * * * ? *")
public class Job2 implements Runnable {
    @Override
    public void run() {
        log.info(new Date() + ": 0/10 * * * * ? *");
    }
}
