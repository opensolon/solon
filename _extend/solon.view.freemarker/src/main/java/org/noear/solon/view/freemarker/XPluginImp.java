package org.noear.solon.view.freemarker;

import freemarker.template.TemplateDirectiveModel;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

import javax.sql.DataSource;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        FreemarkerRender render = FreemarkerRender.global();

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((k, v) -> {
                if (k.startsWith("view:") || k.startsWith("ftl:")) {
                    //java view widget
                    if(TemplateDirectiveModel.class.isAssignableFrom(v.clz())) {
                        render.setSharedVariable(k.split(":")[1], v.raw());
                    }
                }

                if(k.startsWith("share:")){
                    //java share object
                    render.setSharedVariable(k.split(":")[1], v.raw());
                    return;
                }
            });
        });

        Bridge.renderRegister(render);
        Bridge.renderMapping(".ftl", render);
    }
}
