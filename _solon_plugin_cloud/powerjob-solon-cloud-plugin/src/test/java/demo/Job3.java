package demo;

import org.noear.solon.cloud.annotation.CloudJob;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

/**
 * 没有名字的 job ，使用全类名调度
 *
 * @author noear 2023/1/29 created
 */
@CloudJob
public class Job3 implements BasicProcessor {
    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {
        return null;
    }
}
