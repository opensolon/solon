package demo;

import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.handler.IJobHandler;
import org.noear.solon.cloud.annotation.CloudJob;

@CloudJob("job3")
public class Job3 extends IJobHandler {
    @Override
    public void execute() throws Exception {
        XxlJobContext jobContext = XxlJobContext.getXxlJobContext();
    }
}
