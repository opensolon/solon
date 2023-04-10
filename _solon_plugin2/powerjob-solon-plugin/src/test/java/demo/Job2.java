package demo;

import tech.powerjob.solon.annotation.PowerJob;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BroadcastProcessor;

/**
 * @author noear 2023/1/29 created
 */
@PowerJob
public class Job2 implements BroadcastProcessor {
    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {
        return null;
    }
}
