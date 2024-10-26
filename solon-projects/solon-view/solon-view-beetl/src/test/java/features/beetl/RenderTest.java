package features.beetl;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.view.beetl.BeetlRender;

/**
 * @author noear 2024/10/26 created
 */
public class RenderTest {
    @Test
    public void case1() throws Throwable {
        Solon.start(RenderTest.class, new String[0], app -> {

        });


        ModelAndView modelAndView = new ModelAndView("beetl.htm");
        modelAndView.put("msg", "1");
        modelAndView.put("title", "2");

        BeetlRender render = Solon.context().getBean(BeetlRender.class);
        String html = render.renderAndReturn(modelAndView, new ContextEmpty());

        assert html != null;
        assert html.contains("<html>");
    }
}
