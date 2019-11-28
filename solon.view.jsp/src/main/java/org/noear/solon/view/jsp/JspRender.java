package org.noear.solon.view.jsp;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspRender implements XRender {

    private String _baseUri ="/WEB-INF/view/";
    //不要要入参，方便后面多视图混用
    //
    public JspRender(){
        String baseUri = XApp.global().prop().get("slon.mvc.view.prefix");

        if(XUtil.isEmpty(baseUri)==false) {
            _baseUri = baseUri;
        }
    }

    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        if(obj == null){
            return;
        }

        if (obj instanceof ModelAndView) {
            render_mav((ModelAndView) obj, ctx);
        }else{
            ctx.output(obj.toString());
        }
    }

    public void render_mav(ModelAndView mv, XContext ctx) throws Throwable {
        if(ctx.contentTypeNew() == null) {
            ctx.contentType("text/html;charset=utf-8");
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
