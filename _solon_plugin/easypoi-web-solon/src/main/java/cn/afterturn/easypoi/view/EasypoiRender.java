package cn.afterturn.easypoi.view;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;

/**
 * @author noear 2022/10/7 created
 */
public class EasypoiRender implements Render {
    @Override
    public void render(Object data, Context ctx) throws Throwable {
        if (data == null) {
            return;
        }

        ModelAndView mv = (ModelAndView) data;
        PoiBaseView.render(mv.model(), ctx, mv.view());
    }
}
