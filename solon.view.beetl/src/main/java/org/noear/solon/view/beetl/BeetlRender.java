package org.noear.solon.view.beetl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.FileResourceLoader;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XRender;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;

import java.io.File;
import java.net.URI;

public class BeetlRender implements XRender {
    Configuration cfg = null;
    GroupTemplate gt  = null;

    private String _baseUri ="/WEB-INF/view/";
    //不要要入参，方便后面多视图混用
    //
    public BeetlRender() {

        try {
            cfg = Configuration.defaultConfiguration();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }


        String baseUri = XApp.global().prop().get("slon.mvc.view.prefix");

        if(XUtil.isEmpty(baseUri)==false){
            _baseUri = baseUri;
        }


        if (XApp.global().prop().argx().getInt("debug") == 0) {
            initForRuntime();
        }else {
            //添加调试模式
            String dirroot = XUtil.getResource("/").toString().replace("target/classes/", "");
            String dir_str = dirroot + "src/main/resources"+_baseUri;
            File dir = new File(URI.create(dir_str));
            if (!dir.exists()) {
                dir_str = dirroot + "src/main/webapp"+_baseUri;
                dir = new File(URI.create(dir_str));
            }

            try {
                if (dir.exists()) {
                    FileResourceLoader loader = new FileResourceLoader(dir.getAbsolutePath(),"utf-8");
                    gt = new GroupTemplate(loader,cfg);
                }else{
                    initForRuntime();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initForRuntime(){
        try {
            ClasspathResourceLoader loader = new ClasspathResourceLoader(this.getClass().getClassLoader(),_baseUri);
           gt = new GroupTemplate(loader,cfg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void registerTag(String name,Class<?> tag) {
        try {
            gt.registerTag(name,tag);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void setSharedVariable(String name,Object value) {
        try {
            gt.getSharedVars().put(name,value);
        }catch (Exception ex){
            ex.printStackTrace();
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

        ctx.outputAsJson(txt);
    }

    public void render_mav(ModelAndView mv, XContext cxt) throws Exception {
        if(XUtil.isEmpty(mv.view())){
            render_obj(mv, cxt);
            return;
        }

        cxt.contentType("text/html;charset=utf-8");

        Template template = gt.getTemplate(mv.view(), "utf-8");
        template.binding(mv.model());
        template.renderTo(cxt.outputStream());
    }
}
