package org.noear.solon.flow;

import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.Condition;
import org.noear.solon.flow.core.Task;

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
    public int counterGet(String id) {
        return 0;
    }

    @Override
    public void counterIncr(String id, int number) {

    }

    @Override
    public boolean handleCondition(Condition condition) throws Exception {
        return false;
    }

    @Override
    public void handleTask(Task task) throws Exception {

    }


}
