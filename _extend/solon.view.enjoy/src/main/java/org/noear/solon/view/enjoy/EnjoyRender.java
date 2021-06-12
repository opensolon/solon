package org.noear.solon.view.enjoy;

import com.jfinal.template.Directive;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.jfinal.template.source.FileSourceFactory;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.ext.SupplierEx;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;

public class EnjoyRender implements Render {

    private static EnjoyRender _global;

    public static EnjoyRender global() {
        if (_global == null) {
            _global = new EnjoyRender();
        }

        return _global;
    }


    Engine engine = null;
    Engine engine_debug = null;

    private String _baseUri = "/WEB-INF/view/";

    //不要要入参，方便后面多视图混用
    //
    public EnjoyRender() {

        String baseUri = Solon.cfg().get("slon.mvc.view.prefix");

        if (Utils.isEmpty(baseUri) == false) {
            _baseUri = baseUri;
        }


        if (Solon.cfg().isDebugMode()) {
            forDebug();
            forRelease();
        } else {
            forRelease();
        }

        Solon.global().onSharedAdd((k, v) -> {
            putVariable(k, v);
        });
    }

    private void forDebug() {
        if(engine_debug != null){
            return;
        }

        engine_debug = Engine.use();

        //添加调试模式
        String dirroot = Utils.getResource("/").toString().replace("target/classes/", "");
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
                engine_debug.setDevMode(true);
                engine_debug.setBaseTemplatePath(dir.getPath());
                engine_debug.setSourceFactory(new FileSourceFactory());
            } else {
                forRelease();
            }
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    private void forRelease() {
        if(engine != null){
            return;
        }

        engine = Engine.use();

        try {
            engine.setBaseTemplatePath(_baseUri);
            engine.setSourceFactory(new ClassPathSourceFactory());
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    /**
     * 添加共享指令（自定义标签）
     * */
    public void putDirective(String name, Class<? extends Directive> clz) {
        try {
            engine.addDirective(name, clz);

            if(engine_debug != null){
                engine_debug.addDirective(name, clz);
            }
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    /**
     * 添加共享变量
     * */
    public void putVariable(String name, Object value) {
        try {
            engine.addSharedObject(name, value);

            if(engine_debug != null){
                engine_debug.addSharedObject(name, value);
            }
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if (obj == null) {
            return;
        }

        if (obj instanceof ModelAndView) {
            render_mav((ModelAndView) obj, ctx, () -> ctx.outputStream());
        } else {
            ctx.output(obj.toString());
        }
    }

    @Override
    public String renderAndReturn(Object obj, Context ctx) throws Throwable {
        if (obj == null) {
            return null;
        }

        if (obj instanceof ModelAndView) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            render_mav((ModelAndView) obj, ctx, () -> outputStream);

            return outputStream.toString();
        } else {
            return obj.toString();
        }
    }

    public void render_mav(ModelAndView mv, Context ctx, SupplierEx<OutputStream> outputStream) throws Throwable {
        if (ctx.contentTypeNew() == null) {
            ctx.contentType("text/html;charset=utf-8");
        }

        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.view", "EnjoyRender");
        }

        Template template = null;

        if(engine_debug != null) {
            if (engine_debug.getEngineConfig().getDirective(mv.view()) != null) {
                template = engine_debug.getTemplate(mv.view());
            }
        }

        if(template == null) {
            template = engine.getTemplate(mv.view());
        }

        PrintWriter writer = new PrintWriter(outputStream.get());
        template.render(mv.model(), writer);
        writer.flush();
    }
}
