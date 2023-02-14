package org.noear.solon.view.thymeleaf;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.StringWriter;
import java.util.Map;

/**
 * 建议使用：Context::renderAndReturn
 *
 * @deprecated 2.1
 * */
@Deprecated
public class RenderUtil {
    public static String render(String template, Map<String, Object> model) throws Exception {
        TemplateEngine engine = new TemplateEngine();
        StringTemplateResolver resolver = new StringTemplateResolver();
        engine.setTemplateResolver(resolver);
        Context context = new Context();
        context.setVariables(model);
        StringWriter writer = new StringWriter();
        engine.process(template, context, writer);
        return writer.toString();

    }
}

