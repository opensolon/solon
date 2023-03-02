package org.noear.solon.view.jsp;

import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;
import org.noear.solon.view.ViewConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspRender implements Render {
    private static JspRender _global;
    public static JspRender global(){
        if(_global==null){
            _global = new JspRender();
        }

        return _global;
    }

    //不要入参，方便后面多视图混用
    //
    public JspRender(){

    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if(obj == null){
            return;
        }

        if (obj instanceof ModelAndView) {
            render_mav((ModelAndView) obj, ctx);
        }else{
            ctx.output(obj.toString());
        }
    }

    public void render_mav(ModelAndView mv, Context ctx) throws Throwable {
        if(ctx.contentTypeNew() == null) {
            ctx.contentType("text/html;charset=utf-8");
        }

        if (ViewConfig.isOutputMeta()) {
            ctx.headerSet(ViewConfig.HEADER_VIEW_META, "JspRender");
        }

        HttpServletResponse response = (HttpServletResponse)ctx.response();
        HttpServletRequest request = (HttpServletRequest)ctx.request();

        mv.model().forEach(request::setAttribute);

        String view = mv.view();

        if (view.endsWith(".jsp") == true) {

            if (view.startsWith("/") == true) {
                view = ViewConfig.getViewPrefix() + view;
            } else {
                view = ViewConfig.getViewPrefix() + "/" + view;
            }
            view = view.replace("//", "/");
        }

        request.getServletContext()
               .getRequestDispatcher(view)
               .forward(request, response);
    }
}
