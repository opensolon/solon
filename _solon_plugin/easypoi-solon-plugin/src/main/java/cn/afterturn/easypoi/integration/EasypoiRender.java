package cn.afterturn.easypoi.integration;

import cn.afterturn.easypoi.view.PoiBaseView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;

/**
 * poi 渲染器
 *
 * @author noear 2022/10/7 created
 */
public class EasypoiRender implements Render {
    @Override
    public void render(Object data, Context ctx) throws Throwable {
        if (data == null) {
            return;
        }

        if (data instanceof ModelAndView) {
            ModelAndView mv = (ModelAndView) data;
            PoiBaseView.render(mv.model(), ctx, mv.view());
        }
    }
}
