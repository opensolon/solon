package org.noear.solon.view.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XRender;
import org.noear.solon.core.XContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class VelocityRender implements XRender {
    private static VelocityRender _global;
    public static VelocityRender global(){
        if(_global==null){
            _global = new VelocityRender();
        }

        return _global;
    }

    private VelocityEngine velocity = new VelocityEngine();
    private Map<String,Object> _sharedVariable=new HashMap<>();

    private String _baseUri ="/WEB-INF/view/";

    //不要要入参，方便后面多视图混用
    //
    public VelocityRender() {
        String baseUri = XApp.cfg().get("slon.mvc.view.prefix");

        if (XUtil.isEmpty(baseUri) == false) {
            _baseUri = baseUri;
        }



        if (XApp.cfg().isDebugMode()) {
            forDebug();
        }else{
           forRelease();
        }

        velocity.setProperty(Velocity.ENCODING_DEFAULT, getEncoding());
        velocity.setProperty(Velocity.INPUT_ENCODING, getEncoding());

        XApp.cfg().forEach((k, v) -> {
            String key = k.toString();
            if (key.startsWith("veloci")) {
                velocity.setProperty(key, v);
            }
        });

        velocity.init();

        XApp.global().onSharedAdd((k, v) -> {
            setSharedVariable(k, v);
        });
    }

    private void forDebug() {
        String dirroot = XUtil.getResource("/").toString().replace("target/classes/", "");
        File dir = null;

        if (dirroot.startsWith("file:")) {
            String dir_str = dirroot + "src/main/resources" + _baseUri;
            dir = new File(URI.create(dir_str));
            if (!dir.exists()) {
                dir_str = dirroot + "src/main/webapp" + _baseUri;
                dir = new File(URI.create(dir_str));
            }
        }

        try {
            if (dir != null && dir.exists()) {
                velocity.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, dir.getAbsolutePath() + File.separatorChar);
            } else {
                //如果没有找到文件，则使用发行模式
                //
                forRelease();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void forRelease(){
        String root_path = XUtil.getResource(_baseUri).getPath();

        velocity.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, true);
        velocity.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, root_path);
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
            render_mav((ModelAndView) obj, ctx, ctx.outputStream());
        }else{
            ctx.output(obj.toString());
        }
    }

    @Override
    public String renderAndReturn(Object obj, XContext ctx) throws Throwable {
        if(obj == null){
            return null;
        }

        if (obj instanceof ModelAndView) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            render_mav((ModelAndView) obj, ctx, outputStream);

            return outputStream.toString();
        }else{
            return obj.toString();
        }
    }

    public void render_mav(ModelAndView mv, XContext ctx, OutputStream outputStream) throws Exception {
        if(ctx.contentTypeNew() == null) {
            ctx.contentType("text/html;charset=utf-8");
        }

        if(XPluginImp.output_meta){
            ctx.headerSet("solon.view","VelocityRender");
        }

        String view = mv.view();

        //取得velocity的模版
        Template t = velocity.getTemplate(view, getEncoding());

        // 取得velocity的上下文context
        VelocityContext vc = new VelocityContext(mv.model());
        _sharedVariable.forEach((k, v) -> vc.put(k, v));

        // 输出流
        PrintWriter writer = new PrintWriter(outputStream);
        // 转换输出
        t.merge(vc, writer);
        writer.flush();
    }
}
