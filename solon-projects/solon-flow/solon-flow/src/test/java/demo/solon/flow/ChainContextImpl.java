package demo.solon.flow;

import org.noear.solon.flow.core.Condition;
import org.noear.solon.flow.core.Element;
import org.noear.solon.flow.core.Task;
import org.noear.solon.flow.core.ChainContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChainContextImpl implements ChainContext {
    private Map<String, AtomicInteger> counter = new HashMap<>();

    @Override
    public boolean isCancel() {
        return false;
    }

    @Override
    public int counterGet(String id) {
        return counter.computeIfAbsent(id, k -> new AtomicInteger(0))
                .get();
    }

    @Override
    public int counterIncr(String id) {
        return counter.computeIfAbsent(id, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    @Override
    public boolean handleCondition(Element line, Condition condition) throws Exception {
        System.out.println(condition);
        return true;
    }

    @Override
    public void handleTask(Element node, Task task) throws Exception {
        System.out.println(task);
    }
}