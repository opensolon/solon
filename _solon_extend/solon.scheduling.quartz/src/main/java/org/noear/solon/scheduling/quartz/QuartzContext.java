package org.noear.solon.scheduling.quartz;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ContextUtil;
import org.quartz.JobExecutionContext;

import java.util.Map;

/**
 * @author noear
 * @since 2.2
 */
public class QuartzContext {
    /**
     * 获取执行上下文
     * */
    public static Context getContext(JobExecutionContext jc){
        Context ctx = Context.current(); //可能是从上层代理已生成, v1.11
        if (ctx == null) {
            ctx = new ContextEmpty();
            ContextUtil.currentSet(ctx);
        }

        //设置请求对象（mvc 时，可以被注入）
        if(ctx instanceof ContextEmpty) {
            ((ContextEmpty) ctx).request(jc);
        }

        for (Map.Entry<String, Object> kv : jc.getJobDetail().getJobDataMap().entrySet()) {
            if (kv.getValue() != null) {
                ctx.paramMap().put(kv.getKey(), kv.getValue().toString());
            }
        }

        return ctx;
    }
}
