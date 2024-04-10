package org.noear.solon.scheduling.scheduled;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.RankEntity;

import java.util.List;

/**
 * @author noear
 * @since 2.7
 */
public class JobHandlerChain implements JobHandler {
    private Job job;
    private JobHandler handler;
    private List<RankEntity<JobInterceptor>> interceptors;
    private int indexVal;

    public JobHandlerChain(Job job, JobHandler handler, List<RankEntity<JobInterceptor>> interceptors) {
        this.job = job;
        this.handler = handler;
        this.interceptors = interceptors;
        this.indexVal = 0;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        if (interceptors.size() > indexVal) {
            interceptors.get(indexVal++).target.doIntercept(job, this);
        } else {
            handler.handle(ctx);
        }
    }
}
