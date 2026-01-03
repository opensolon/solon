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

    /**
     * 先完整渲染整个模板到内存中，没有错误时，再一次性写入 HTTP 响应。
     */
    @Test
    public void case2() throws Throwable {
        Solon.start(RenderTest.class, new String[0], app -> {

        });


        ModelAndView modelAndView = new ModelAndView("beetl_out_error.htm");
        modelAndView.put("title", "beetl");

        BeetlRender render = Solon.context().getBean(BeetlRender.class);
        String html = null;
        try {
            html = render.renderAndReturn(modelAndView, new ContextEmpty());
            assert html != null;
        } catch (Throwable e) {
            assert html == null;// 没有内容输出，即渲染中途报错没有内容输出
            assert e.getMessage() != null;
            assert e.getMessage().contains("VAR_NOT_DEFINED");
        }
    }
}
