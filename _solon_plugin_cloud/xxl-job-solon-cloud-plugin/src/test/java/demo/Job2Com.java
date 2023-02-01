package demo;

import com.xxl.job.core.context.XxlJobContext;
import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.annotation.CloudJob;

@Component
public class Job2Com {

    @CloudJob("job2")
    public void job2(XxlJobContext xxlJobContext){

    }
}
