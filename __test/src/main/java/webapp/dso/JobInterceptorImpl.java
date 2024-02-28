package webapp.dso;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.logging.utils.TagsMDC;
import org.noear.solon.scheduling.scheduled.Job;
import org.noear.solon.scheduling.scheduled.JobHandler;
import org.noear.solon.scheduling.scheduled.JobInterceptor;

/**
 * @author noear 2024/2/28 created
 */
@Slf4j
@Component
public class JobInterceptorImpl implements JobInterceptor {
    @Override
    public void doIntercept(Job job, JobHandler handler) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            handler.handle(job.getContext());
        } catch (Throwable e) {
            //记录日志
            TagsMDC.tag0("job");
            TagsMDC.tag1(job.getName());
            log.error("{}", e);

            throw e; //别吃掉
        } finally {
            //记录一个内部处理的花费时间
            long timespan = System.currentTimeMillis() - start;
            System.out.println("JobInterceptor: job=" + job.getName());
        }
    }
}
