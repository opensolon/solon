package webapp.demo2_mvc;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XSingleton;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

//使用自定义渲染器（实现XRender接口）
@XSingleton(false)
@XController
public class ViewRenderController implements XRender {
    @Override
    public void render(Object obj, XContext ctx) throws Exception {
        //自定义渲染
        if (obj != null) {
            ctx.output(obj.toString());
        }
    }

    @XMapping("/demo2/dock2")
    public ModelAndView dock(){
        ModelAndView model = new ModelAndView("dock.ftl");
        model.put("title","dock");
        model.put("msg","你好 world! in XController");

        return model;
    }
}
