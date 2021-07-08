package webapp.demo9_multiple_view;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.i18n.annotation.I18n;

/**
 * 实现简单的 mvc 效果
 * */

@Controller
public class FreemarkerView {

    @I18n
    @Mapping("/demo9/view/ftl")
    public ModelAndView view() {
        ModelAndView model = new ModelAndView("freemarker.ftl");
        model.put("title", "dock");
        model.put("msg", "你好 world! in XController");

        return model;
    }
}
