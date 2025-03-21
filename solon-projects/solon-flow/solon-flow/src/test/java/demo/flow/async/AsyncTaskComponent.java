package demo.flow.async;

import org.noear.solon.core.util.RunUtil;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.Node;
import org.noear.solon.flow.TaskComponent;

public abstract class AsyncTaskComponent implements TaskComponent {
    @Override
    public void run(ChainContext context, Node node) throws Throwable {
        context.interrupt();

        RunUtil.async(() -> {
            try {
                asyncRun(context, node);
            } catch (Throwable err) {
                onError(context, node, err);
            }
        });
    }

    protected void onError(ChainContext context, Node node, Throwable err) {
        context.stop();
    }

    protected abstract void asyncRun(ChainContext context, Node node) throws Throwable;
}