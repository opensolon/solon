package org.noear.solon.view.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.StringWriter;

public class XRenderUtil {
    public static String reander(String template, Object model) throws Exception {
        StringWriter writer = new StringWriter();
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setNumberFormat("#");

        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("template", template);
        configuration.setTemplateLoader(stringLoader);

        Template tmpl = configuration.getTemplate("template", "utf-8");

        tmpl.process(model, writer);
        return writer.toString();
    }
}
