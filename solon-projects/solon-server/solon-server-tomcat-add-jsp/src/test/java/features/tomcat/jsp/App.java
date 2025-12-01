package features.tomcat.jsp;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;

/**
 *
 * @author noear 2025/12/1 created
 *
 */
@Controller
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args);
    }

    @Mapping("/")
    public ModelAndView home() {
        ModelAndView model = new ModelAndView("jsp.jsp");
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        return model;
    }
}
