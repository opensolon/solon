package org.noear.solon.view.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import java.io.StringWriter;
import java.util.Map;

public class RenderUtil {
    static VelocityEngine engine = null;

    public static String render(String template, Map<String, Object> model) throws Exception {

        if (engine == null) {
            engine = new VelocityEngine();
            engine.init();
        }
        VelocityContext context = new VelocityContext(model);

        StringWriter writer = new StringWriter();

        engine.evaluate(context, writer, "", template);

        return writer.toString();
    }
}
