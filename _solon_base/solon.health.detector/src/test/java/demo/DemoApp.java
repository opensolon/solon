package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.health.detector.Detector;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2021/12/21 created
 */
@Component
public class DemoApp implements Detector {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args, app -> {
            app.get("/", c -> c.redirect("/healthz"));
        });
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Map<String, Object> getInfo() {
        return new LinkedHashMap<>();
    }
}
