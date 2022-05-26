package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.ids.context.IdsContext;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.handle.ModelAndView;

@Controller
@Mapping("/")
public class HomeController extends IdsController {

    @Inject
    IdsContext context;

    @Mapping(method = MethodType.GET)
    public ModelAndView index(ModelAndView mv) {
        //返回模型与视图，会被视图引擎渲染后再输出，默认是html格式
        mv.put(
                "clientDetails",
                this.context.getClientDetailService().getAllClientDetail()
        );

        return mv.view("index.html");
    }
}
