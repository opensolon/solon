package webapp.demo9_multiple_view;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

/**
 * 实现简单的 mvc 效果
 * */
@Mapping("/demo9/view/jsp")
@Controller
public class JspView implements Handler {
    @Override
    public void handle(Context ctx) throws Throwable {
        ModelAndView model = new ModelAndView("jsp.jsp");
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        ctx.render(model);
    }
}
