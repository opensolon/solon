package webapp.demo9_multiple_view;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

@Controller
public class EnjoyView extends ViewBase {
    @Mapping("/demo9/view/enjoy")
    public ModelAndView view(ModelAndView model) {
        model.view("enjoy.shtm");

        model.put("title", "dock");
        model.put("msg", "你好 world! in XController");

        return model;
    }
}
