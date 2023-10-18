package labs.importTest.case2.maina;

import labs.importTest.case2.AutoConfig;
import labs.importTest.case2.AutoConfig2;
import labs.importTest.case2.DemoConfig;
import org.noear.solon.Solon;

/**
 * @author noear 2023/10/18 created
 */
public class ImportTestC {
    public static void main(String[] args) {
        Solon.start(ImportTestC.class, args, app->{
            app.context().beanMake(AutoConfig2.class);
        });

        DemoConfig demoConfig = Solon.context().getBean(DemoConfig.class);
        System.out.println(":::" + demoConfig);
        assert demoConfig == null;
    }
}
