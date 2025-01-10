package org.noear.solon.flow;

import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.Condition;
import org.noear.solon.flow.core.Task;

import java.util.List;

/**
 * @author noear
 * @since 3.0
 */
public class SolonChainContext implements ChainContext {
    @Override
    public boolean is_cancel() {
        return false;
    }

    @Override
    public boolean condition_handle(Condition condition) throws Exception {
        return false;
    }

    @Override
    public void task_handle(List<Task> tasks) throws Exception {

    }
}
