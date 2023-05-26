package demo;

import org.noear.solon.cloud.CloudJobHandler;
import org.noear.solon.cloud.annotation.CloudJob;
import org.noear.solon.core.handle.Context;
import tech.powerjob.worker.core.processor.TaskContext;

/**
 * 会转为 PowerJobProxy implements BasicProcessor 处理
 *
 * @author noear 2023/1/29 created
 */
@CloudJob("job1")
public class Job1 implements CloudJobHandler {
    @Override
    public void handle(Context ctx) throws Throwable {
        //拿到 TaskContext
        TaskContext taskContext = (TaskContext)ctx.request();
    }
}
