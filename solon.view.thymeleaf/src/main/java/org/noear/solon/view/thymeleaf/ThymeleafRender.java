package org.noear.solon.view.thymeleaf;

import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.Aop;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ThymeleafRender implements XRender {
    private static ThymeleafRender _global;
    public static ThymeleafRender global(){
        if(_global==null){
            _global = new ThymeleafRender();
        }

        return _global;
    }



    private TemplateEngine _engine = new TemplateEngine();

    private Map<String,Object> _sharedVariable = new HashMap<>();

    private String _baseUri ="/WEB-INF/view/";

    public ThymeleafRender(){
        String baseUri = XApp.global().prop().get("slon.mvc.view.prefix");

        if(XUtil.isEmpty(baseUri)==false){
            _baseUri = baseUri;
        }

        if (Aop.prop().argx().getInt("debug") == 0) {
            forRelease();
        }else {
            forDebug();
        }


        try {
            XApp.global().shared().forEach((k, v)->{
                setSharedVariable(k,v);
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        XApp.global().onSharedAdd((k,v)->{
            setSharedVariable(k,v);
        });
    }

    private void forDebug(){
        String dirroot = XUtil.getResource("/").toString().replace("target/classes/", "");
        String dir_str = dirroot + "src/main/resources"+_baseUri;
        File dir = new File(URI.create(dir_str));
        if (!dir.exists()) {
            dir_str = dirroot + "src/main/webapp"+_baseUri;
            dir = new File(URI.create(dir_str));
        }

        try {
            if (dir.exists()) {
                FileTemplateResolver _loader = new FileTemplateResolver();
                _loader.setPrefix(dir.getAbsolutePath() + File.separatorChar);
                _loader.setTemplateMode(TemplateMode.HTML);
                _loader.setCacheable(true);
                _loader.setCharacterEncoding("utf-8");
                _loader.setCacheTTLMs(Long.valueOf(3600000L));

                _engine.setTemplateResolver(_loader);
            }else{
                //如果没有找到文件，则使用发行模式
                //
                forRelease();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void forRelease(){
        ClassLoaderTemplateResolver _loader = new ClassLoaderTemplateResolver();

        _loader.setPrefix(_baseUri);
        _loader.setTemplateMode(TemplateMode.HTML);
        _loader.setCacheable(true);
        _loader.setCharacterEncoding("utf-8");
        _loader.setCacheTTLMs(Long.valueOf(3600000L));

        _engine.setTemplateResolver(_loader);
    }



    public void setSharedVariable(String name,Object obj){
        _sharedVariable.put(name, obj);
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
        ctx.contentType("text/html;charset=utf-8");

        Context context = new Context();
        context.setVariables(_sharedVariable);
        context.setVariables(mv);


        PrintWriter writer = new PrintWriter(ctx.outputStream());

        _engine.process(mv.view(), context,writer);

    }
}
