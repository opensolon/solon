package demo;

import tech.powerjob.solon.annotation.PowerJob;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

/**
 * @author noear 2023/1/29 created
 */
@PowerJob("job1")
public class Job1 implements BasicProcessor {
    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {
        return null;
    }
}
