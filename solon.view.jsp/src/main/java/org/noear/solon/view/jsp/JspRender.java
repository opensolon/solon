package org.noear.solon.view.jsp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
    public void render(Object obj, XContext ctx) throws Exception {
        if(obj instanceof ModelAndView){
            render_mav((ModelAndView)obj, ctx);
        }else {
            render_obj(obj,ctx);
        }
    }


    private void render_obj(Object obj, XContext ctx) throws Exception{
        boolean is_rpc = "service".equals(ctx.attr("solon.reader.source",null ));

        if(is_rpc == false){
            if(obj instanceof String){
                ctx.output((String) obj);
                return;
            }

            if(obj instanceof Exception){
                throw (Exception) obj;
            }
        }

        String txt = null;
        if (is_rpc) {
            txt = JSON.toJSONString(obj,
                    SerializerFeature.BrowserCompatible,
                    SerializerFeature.WriteClassName,
                    SerializerFeature.DisableCircularReferenceDetect);
        } else {
            txt = JSON.toJSONString(obj,
                    SerializerFeature.BrowserCompatible,
                    SerializerFeature.DisableCircularReferenceDetect);
        }

        ctx.attrSet("output", txt);
        ctx.outputAsJson(txt);
    }

    public void render_mav(ModelAndView mv, XContext context) throws Exception {
        HttpServletResponse response = (HttpServletResponse)context.response();
        HttpServletRequest request = (HttpServletRequest)context.request();

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
