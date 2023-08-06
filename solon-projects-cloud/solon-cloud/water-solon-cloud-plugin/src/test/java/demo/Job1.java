package demo;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.core.handle.Context;

@CloudJob("job1")
public class Job1 implements CloudJobHandler {
    @Override
    public void handle(Context ctx) throws Throwable {

    }
}
