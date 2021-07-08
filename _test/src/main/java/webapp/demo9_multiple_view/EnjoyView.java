package webapp.demo9_multiple_view;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.i18n.annotation.I18n;

@I18n
@Controller
public class EnjoyView {

    @Mapping("/demo9/view/enjoy")
    public ModelAndView view() {
        ModelAndView model = new ModelAndView("enjoy.shtm");
        model.put("title", "dock");
        model.put("msg", "你好 world! in XController");

        return model;
    }
}
