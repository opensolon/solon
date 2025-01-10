package org.noear.solon.flow;

import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.Condition;
import org.noear.solon.flow.core.Task;

import java.util.Collection;

/**
 * @author noear
 * @since 3.0
 */
public class SolonChainContext implements ChainContext {
    @Override
    public boolean isCancel() {
        return false;
    }

    @Override
    public boolean conditionHandle(Condition condition) throws Exception {
        return false;
    }

    @Override
    public void taskHandle(Collection<Task> tasks) throws Exception {

    }
}
