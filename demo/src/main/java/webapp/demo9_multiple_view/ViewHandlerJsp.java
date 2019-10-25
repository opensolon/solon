package webapp.demo9_multiple_view;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XHandler;

/**
 * 实现简单的 mvc 效果
 * */
@XMapping("/demo9/view/jsp")
@XController
public class ViewHandlerJsp implements XHandler {
    @Override
    public void handle(XContext ctx) throws Throwable {
        ModelAndView model = new ModelAndView("dock.jsp");
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        ctx.render(model);
    }
}
