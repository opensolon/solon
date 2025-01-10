package demo.solon.flow;

import org.noear.solon.flow.core.Condition;
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
    public void counterIncr(String id) {
        counter.computeIfAbsent(id, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    @Override
    public boolean handleCondition(Condition condition) throws Exception {
        System.out.println(condition.expr());

        return true;
    }

    @Override
    public void handleTask(Task task) throws Exception {

    }


}