package org.noear.solon.cloud;

import org.noear.solon.cloud.model.Job;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.RankEntity;

import java.util.List;

/**
 * @author noear
 * @since 2.7
 */
public class CloudJobHandlerChain implements CloudJobHandler{
    private Job job;
    private CloudJobHandler handler;
    private List<RankEntity<CloudJobInterceptor>> interceptors;
    private int indexVal;

    public CloudJobHandlerChain(Job job, CloudJobHandler handler, List<RankEntity<CloudJobInterceptor>> interceptors) {
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
