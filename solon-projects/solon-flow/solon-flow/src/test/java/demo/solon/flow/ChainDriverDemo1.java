package demo.solon.flow;

import org.noear.liquor.eval.Exprs;
import org.noear.solon.flow.core.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChainDriverDemo1 implements ChainDriver {
    private final Map<String, Object> context;
    private Map<String, AtomicInteger> counter = new HashMap<>();

    public ChainDriverDemo1(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public boolean isInterrupt() {
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
        return (boolean) Exprs.eval(condition.expr(), context);
    }

    @Override
    public void handleTask(Element node, Task task) throws Exception {
        System.out.println(task);
        Object rst = Exprs.eval(task.expr(), context);
        System.out.println("rst: " + rst);
    }
}