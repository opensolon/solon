package demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

import javax.servlet.http.HttpServletRequest;

/**
 * @author noear 2022/1/18 created
 */
@Controller
public class DemoTest {
    @Mapping("demo")
    public void demo(Context ctx, HttpServletRequest request) {
        request = new HttpServletRequestWrapperImpl(ctx, request);
    }
}
