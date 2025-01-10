package org.noear.solon.flow;

import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.Condition;
import org.noear.solon.flow.core.Element;
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
    public int counterIncr(String id) {
        return 0;
    }

    @Override
    public boolean handleCondition(Element line, Condition condition) throws Exception {
        return false;
    }

    @Override
    public void handleTask(Element node, Task task) throws Exception {

    }


}
