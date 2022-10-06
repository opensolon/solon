package org.noear.solon.view.jsp;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

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

    private String _baseUri ="/WEB-INF/view/";
    //不要要入参，方便后面多视图混用
    //
    public JspRender(){
        String baseUri = Solon.cfg().get("slon.mvc.view.prefix");

        if(Utils.isEmpty(baseUri)==false) {
            _baseUri = baseUri;
        }
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
            ctx.contentTypeSet("text/html;charset=utf-8");
        }

        if(XPluginImp.output_meta){
            ctx.headerSet("Solon-View","JspRender");
        }


        HttpServletResponse response = (HttpServletResponse)ctx.response();
        HttpServletRequest request = (HttpServletRequest)ctx.request();

        mv.model().forEach(request::setAttribute);

        String view = mv.view();

        if (view.endsWith(".jsp") == true) {

            if (view.startsWith("/") == true) {
                view = _baseUri + view;
            } else {
                view = _baseUri + "/" + view;
            }
            view = view.replace("//", "/");
        }

        request.getServletContext()
               .getRequestDispatcher(view)
               .forward(request, response);
    }
}
