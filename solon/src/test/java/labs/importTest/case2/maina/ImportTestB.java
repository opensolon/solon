package labs.importTest.case2.maina;

import labs.importTest.case2.DemoConfig;
import labs.importTest.case2.EnableDemo;
import org.noear.solon.Solon;

/**
 * @author noear 2023/10/18 created
 */
@EnableDemo
public class ImportTestB {
    public static void main(String[] args) {
        Solon.start(ImportTestB.class, args);

        DemoConfig demoConfig = Solon.context().getBean(DemoConfig.class);
        System.out.println(":::" + demoConfig.getDemoName());
        assert "a".equals(demoConfig.getDemoName());
    }
}
