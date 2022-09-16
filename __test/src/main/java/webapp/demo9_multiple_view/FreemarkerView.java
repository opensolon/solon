package webapp.demo9_multiple_view;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

/**
 * 实现简单的 mvc 效果
 * */

@Controller
public class FreemarkerView extends ViewBase {
    @Mapping("/demo9/view/ftl")
    public ModelAndView view(ModelAndView model) {
        model.view("freemarker.ftl");

        model.put("title", "dock");
        model.put("msg", "你好 world!");

        return model;
    }
}
