package org.noear.solon.scheduling.simple.test.features;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2021/12/28 created
 */
@Slf4j
@Component
public class JobOfMethod {
    @Scheduled(fixedRate = 1000 * 3)
    public void job21() {
        log.debug(new Date() + ": 1000*3");
    }

    @Scheduled(cron = "1s")
    public void job22() {
        log.debug(new Date() + ": 1s");
    }

    @Scheduled(cron = "0/10 * * * * ? *", zone = "+00")
    public void job23() {
        log.debug(new Date() + ": 0/10 * * * * ? *");
    }

    @Scheduled(cron = "0/10 * * * * ? *", zone = "CET")
    public void job24() {
        log.debug(new Date() + ": 0/10 * * * * ? *");
    }

    @Scheduled(cron = "0/10 * * * * ? *", zone = "Asia/Shanghai")
    public void job25() {
        log.debug(new Date() + ": 0/10 * * * * ? *");
    }
}