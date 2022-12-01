package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author noear 2022/12/1 created
 */
@Configuration
public class Config {
    @Bean
    public Scheduler initQuartz() throws SchedulerException {
        SchedulerFactory factory = new StdSchedulerFactory();
        return factory.getScheduler();
    }
}
