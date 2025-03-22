package demo.flow.async;

import org.noear.solon.core.util.RunUtil;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.Node;
import org.noear.solon.flow.TaskComponent;

/**
 * 任务组件增强（同时支持同步也异步）
 * */
public abstract class TaskComponentPlus implements TaskComponent {
    @Override
    public void run(ChainContext context, Node node) throws Throwable {
        //通过 meta:{async:true} 来启用
        boolean async = node.metaOrDefault("async", false);

        if (async) {
            context.interrupt();

            RunUtil.async(() -> {
                try {
                    doRun(context, node, true);
                } catch (Throwable err) {
                    onError(context, node, err);
                }
            });
        } else {
            doRun(context, node, false);
        }
    }

    protected void onError(ChainContext context, Node node, Throwable err) {
        context.stop();
    }

    protected abstract void doRun(ChainContext context, Node node, boolean async) throws Throwable;
}