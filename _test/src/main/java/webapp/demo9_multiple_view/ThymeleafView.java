package webapp.demo9_multiple_view;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handler.ModelAndView;
import org.noear.solon.core.handler.Context;
import org.noear.solon.core.handler.Handler;

@Mapping("/demo9/view/thymeleaf")
@Controller
public class ThymeleafView implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        ModelAndView model = new ModelAndView("thymeleaf.html");
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        ctx.render(model);
    }
}
