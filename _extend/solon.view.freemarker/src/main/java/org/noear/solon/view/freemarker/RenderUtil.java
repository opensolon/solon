package org.noear.solon.view.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.noear.solon.Solon;

import java.io.StringWriter;

public class RenderUtil {
    public static String render(String template, Object model) throws Exception {
        StringWriter writer = new StringWriter();
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setNumberFormat("#");
        cfg.setDefaultEncoding("utf-8");

        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("template", template);
        cfg.setTemplateLoader(stringLoader);

        Template tmpl = cfg.getTemplate("template", Solon.encoding());

        tmpl.process(model, writer);
        return writer.toString();
    }
}
