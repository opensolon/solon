package features.enjoy;

import org.junit.jupiter.api.Test;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.test.SolonTest;
import org.noear.solon.view.enjoy.EnjoyRender;

@SolonTest(InjectTest.class)
public class InjectTest {
    public static void main(String[] args) {
        Solon.start(InjectTest.class, args);
    }

    @Test
    public void testViewDirectiveInject() throws Throwable {
        EnjoyRender render = Solon.context().getBean(EnjoyRender.class);

        ModelAndView mv = new ModelAndView("inject_test.shtm");
        mv.put("name", "Solon");
        String html = render.renderAndReturn(mv, new ContextEmpty());

        System.out.println(html);

        // HelloTag 通过 @Component("view:hello") 注册，由 ViewEnjoyPlugin 扫描并注入
        assert html.contains("share: Hi, Solon!; directive: Hi, Solon!") : "View directive injection failed: " + html;
    }
}
