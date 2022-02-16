package org.noear.solon.plugin.jap.ids.http.controller;

import com.fujieid.jap.ids.context.IdsContext;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@Mapping("/")
public class HomeController extends BaseController {

    @Inject
    IdsContext context;

    @Mapping(method = MethodType.GET)
    public ModelAndView index() {
        //返回模型与视图，会被视图引擎渲染后再输出，默认是html格式
        Map<String, Object> map = new HashMap<>();
        map.put(
                "clientDetails",
                this.context.getClientDetailService().getAllClientDetail()
        );

        return new ModelAndView("index.html", map);
    }

}
