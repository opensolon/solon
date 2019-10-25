package org.noear.solon.view.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.noear.solon.XApp;
import org.noear.solon.XProperties;
import org.noear.solon.XUtil;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XRender;
import org.noear.solon.core.XContext;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class VelocityRender implements XRender {

    private VelocityEngine velocity = new VelocityEngine();
    private Map<String,Object> _sharedVariable=new HashMap<>();

    private String _baseUri ="/WEB-INF/view/";

    //不要要入参，方便后面多视图混用
    //
    public VelocityRender(){
        XProperties props = XApp.global().prop();

        String baseUri = props.get("slon.mvc.view.prefix");

        if(XUtil.isEmpty(baseUri)==false){
            _baseUri = baseUri;
        }

        String root_path = XUtil.getResource(_baseUri).getPath();

        velocity.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, true);
        velocity.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, root_path);
        velocity.setProperty(Velocity.ENCODING_DEFAULT, getEncoding());
        velocity.setProperty(Velocity.INPUT_ENCODING, getEncoding());

        if (props != null) {
            props.forEach((k, v) -> {
                String key = k.toString();
                if (key.startsWith("veloci")) {
                    velocity.setProperty(key, v);
                }
            });
        }

        velocity.init();

        XApp.global().onSharedAdd((k,v)->{
            setSharedVariable(k,v);
        });
    }

    public void loadDirective(Object obj){
        velocity.loadDirective(obj.getClass().getName());
    }

    public void setSharedVariable(String key, Object obj){
        _sharedVariable.put(key, obj);
    }

    public String getEncoding(){
        return "utf-8";
    }

    @Override
    public void render(Object obj, XContext ctx) throws Exception {
        if(obj == null){
            return;
        }

        if (obj instanceof ModelAndView) {
            render_mav((ModelAndView) obj, ctx);
        }else{
            ctx.output(obj.toString());
        }
    }

    public void render_mav(ModelAndView mv, XContext cxt) throws Exception {
        cxt.contentType("text/html;charset=utf-8");

        String view =  mv.view();

        //取得velocity的模版
        Template t = velocity.getTemplate(view, getEncoding());

        // 取得velocity的上下文context
        VelocityContext vc = new VelocityContext(mv.model());
        _sharedVariable.forEach((k,v)->vc.put(k, v));

        // 输出流
        PrintWriter writer = new PrintWriter(cxt.outputStream());
        // 转换输出
        t.merge(vc, writer);
    }
}
