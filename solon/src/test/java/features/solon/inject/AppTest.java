package features.solon.inject;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2024/12/17 created
 */
public class AppTest {
    @Test
    public void case1() throws Exception {
        Solon.start(AppTest.class, new String[0], app -> {
            app.pluginAdd(0, context -> {
                context.beanInjectorAdd(Inject.class, IA.class, (vh, anno) -> {
                    vh.setValueDefault(() -> new IAExtDef());
                    vh.context().getBeanAsync(vh.getType(), bean -> {
                        vh.setValue(bean);
                    });
                });
            });
        });

        ComA comA = Solon.context().getBean(ComA.class);
        ComB comB = Solon.context().getBean(ComB.class);

        assert comA != null;
        assert comB != null;

        System.out.println(comA.ia);
        System.out.println(comB.ia);

        assert comA.ia instanceof IAExtDef;
        assert comB.ia instanceof IAExtImpl;
    }
}