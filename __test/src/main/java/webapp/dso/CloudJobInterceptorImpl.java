package webapp.dso;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.CloudJobInterceptor;
import org.noear.solon.cloud.model.Job;
import org.noear.solon.logging.utils.TagsMDC;

/**
 * @author noear 2022/4/10 created
 */
@Slf4j
public class CloudJobInterceptorImpl implements CloudJobInterceptor {
    @Override
    public void doIntercept(Job job, CloudJobHandler handler) throws Throwable {
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
            CloudClient.metric().addMeter(Solon.cfg().appName(), "job", timespan);
        }
    }
}
