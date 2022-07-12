package webapp.demo9_multiple_view;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.i18n.annotation.I18n;

/**
 * 实现简单的 mvc 效果
 * */
@I18n
@Controller
public class JspView  {
    @Mapping("/demo9/view/jsp")
    public ModelAndView view() {
        ModelAndView model = new ModelAndView("jsp.jsp");
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        return model;
    }
}
