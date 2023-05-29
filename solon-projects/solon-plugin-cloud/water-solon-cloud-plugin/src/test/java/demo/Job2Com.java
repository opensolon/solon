package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.core.handle.Context;

@Component
public class Job2Com {
    @CloudJob("job2")
    public void job2(Context ctx){

    }
}
