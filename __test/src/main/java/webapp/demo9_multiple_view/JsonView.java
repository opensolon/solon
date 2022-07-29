package webapp.demo9_multiple_view;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.handle.ModelAndView;

@Singleton(false)
@Controller
public class JsonView extends ViewBase{
    @Mapping("/demo9/view/json")
    public ModelAndView view(){
        ModelAndView model = new ModelAndView();
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        return model;
    }
}
