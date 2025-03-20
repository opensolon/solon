package features.flow.cfg_test;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.Chain;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.FlowEngine;
import org.noear.solon.flow.driver.MapChainDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2025/3/20 created
 */
public class FlowTest {
    @Test
    public void case1() throws Throwable {
        MapChainDriver driver = new MapChainDriver();
        driver.putComponent("a", (c, o) -> {
            ((List) c.get("log")).add(o.title());
        });

        FlowEngine flow = FlowEngine.newInstance();
        flow.register(driver);
        flow.load(Chain.parseByUri("classpath:flow/flow_case8.chain.yml"));

        ChainContext context = new ChainContext();
        context.put("log", new ArrayList<>());
        context.put("dataType", "1");

        flow.eval("f8", context);

        String log = context.get("log").toString();
        System.out.println(log);
        assert "[数据预处理, 元数据填充, 瞬时数据, 构建转发数据, Http转发, Mqtt转发]".equals(log);


        System.out.println("---------------------");

        context = new ChainContext();
        context.put("log", new ArrayList<>());
        context.put("dataType", "type1");

        flow.eval("f8", context);

         log = context.get("log").toString();
        System.out.println(log);
        assert "[数据预处理, 元数据填充, 汇总数据, 构建转发数据, Http转发, Mqtt转发, 汇总统计]".equals(log);
    }
}