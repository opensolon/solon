package features.thymeleaf;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.view.thymeleaf.ThymeleafRender;

/**
 * @author noear 2024/10/26 created
 */
public class RenderTest {
    @Test
    public void case1() throws Throwable {
        Solon.start(RenderTest.class, new String[0], app -> {

        });

        ModelAndView modelAndView = new ModelAndView("thymeleaf.html");
        modelAndView.put("msg", "1");
        modelAndView.put("title", "2");

        ThymeleafRender render = Solon.context().getBean(ThymeleafRender.class);
        String html = render.renderAndReturn(modelAndView, new ContextEmpty());

        assert html != null;
        assert html.contains("<html>");
    }
}
