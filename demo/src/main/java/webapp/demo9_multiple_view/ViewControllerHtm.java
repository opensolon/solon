package webapp.demo9_multiple_view;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.ModelAndView;

@XSingleton(false)
@XController
public class ViewControllerHtm {
    @XMapping("/demo9/view/htm")
    public ModelAndView dock(){
        ModelAndView model = new ModelAndView("dock.htm");
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        return model;
    }
}
