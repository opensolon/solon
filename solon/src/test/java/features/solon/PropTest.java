package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author noear 2025/6/5 created
 */
public class PropTest {
    @Test
    public void case1() throws Throwable {
        AtomicReference<String> ref4 = new AtomicReference<>();
        AtomicReference<String> ref2 = new AtomicReference<>();
        AtomicReference<String> ref0 = new AtomicReference<>();

        SimpleSolonApp app = new SimpleSolonApp(PropTest.class, new String[]{});
        app.start(x -> {
            x.enableScanning(false);

            {
                Properties props = new Properties();
                props.setProperty("text", "${demo:a}");
                x.cfg().loadAdd(props);
            }

            x.pluginAdd(4, context -> {
                ref4.set(x.cfg().get("text")); //a
            });

            x.pluginAdd(3, context -> {
                Properties props = new Properties();
                props.setProperty("demo", "b");
                x.cfg().loadAdd(props);
            });

            x.pluginAdd(2, context -> {
                ref2.set(x.cfg().get("text")); //b
            });

            x.pluginAdd(1, context -> {
                Properties props = new Properties();
                props.setProperty("demo", "c");
                x.cfg().loadAdd(props);
            });

            x.pluginAdd(0, context -> {
                ref0.set(x.cfg().get("text")); //c
            });
        });

        System.out.println(ref4.get());
        System.out.println(ref2.get());
        System.out.println(ref0.get());

        assert "a".equals(ref4.get());
        assert "b".equals(ref2.get());
        assert "c".equals(ref0.get());
    }
}
