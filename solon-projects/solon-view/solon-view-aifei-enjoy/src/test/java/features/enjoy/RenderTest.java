package features.enjoy;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.test.SolonTest;
import org.noear.solon.view.enjoy.EnjoyRender;

/**
 * @author noear 2024/10/26 created
 */
@SolonTest(RenderTest.class)
public class RenderTest {
    @Test
    public void case1() throws Throwable {
        ModelAndView modelAndView = new ModelAndView("enjoy.shtm");
        modelAndView.put("msg", "1");
        modelAndView.put("title", "2");

        EnjoyRender render = Solon.context().getBean(EnjoyRender.class);
        String html = render.renderAndReturn(modelAndView, new ContextEmpty());

        assert html != null;
        assert html.contains("<html>");
    }
}
