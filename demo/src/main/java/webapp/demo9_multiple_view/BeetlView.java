package webapp.demo9_multiple_view;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.ModelAndView;

@XSingleton(false)
@XController
public class BeetlView {
    @XMapping("/demo9/view/beetl")
    public ModelAndView dock(){
        ModelAndView model = new ModelAndView("beetl.htm");
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        return model;
    }
}
