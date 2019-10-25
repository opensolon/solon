package org.noear.solon.view.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.noear.solon.XApp;
import org.noear.solon.XUtil;
import org.noear.solon.core.XRender;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;

public class FreemarkerRender implements XRender {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

    private String _baseUri ="/WEB-INF/view/";
    //不要要入参，方便后面多视图混用
    //
    public FreemarkerRender() {
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
                    cfg.setDirectoryForTemplateLoading(dir);
                }else{
                    initForRuntime();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        cfg.setNumberFormat("#");
        cfg.setDefaultEncoding("utf-8");

        XApp.global().onSharedAdd((k,v)->{
            setSharedVariable(k,v);
        });
    }

    private void initForRuntime(){
        try {
            cfg.setClassForTemplateLoading(this.getClass(), _baseUri);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(0, Integer.MAX_VALUE));
    }

    public void setSharedVariable(String name,Object value) {
        try {
            cfg.setSharedVariable(name, value);
        }catch (Exception ex){
            ex.printStackTrace();
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

    public void render_mav(ModelAndView mv, XContext cxt) throws Throwable {
        cxt.contentType("text/html;charset=utf-8");

        PrintWriter writer = new PrintWriter(cxt.outputStream());

        Template template = cfg.getTemplate(mv.view(), "utf-8");

        template.process(mv.model(), writer);
    }
}
