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
import java.net.URL;

public class EnjoyRender implements Render {

    private static EnjoyRender _global;

    public static EnjoyRender global() {
        if (_global == null) {
            _global = new EnjoyRender();
        }

        return _global;
    }


    Engine provider = null;
    Engine provider_debug = null;

    private String _baseUri = "/WEB-INF/view/";

    //不要要入参，方便后面多视图混用
    //
    public EnjoyRender() {

        String baseUri = Solon.cfg().get("slon.mvc.view.prefix");

        if (Utils.isEmpty(baseUri) == false) {
            _baseUri = baseUri;
        }

        forDebug();
        forRelease();

        Solon.app().onSharedAdd((k, v) -> {
            putVariable(k, v);
        });
    }

    private void forDebug() {
        if(Solon.cfg().isDebugMode() == false) {
            return;
        }

        if(provider_debug != null){
            return;
        }

        //添加调试模式
        URL rooturi = Utils.getResource("/");
        if(rooturi == null){
            return;
        }

        provider_debug = Engine.create("debug");


        String rootdir = rooturi.toString().replace("target/classes/", "");
        File dir = null;

        if (rootdir.startsWith("file:")) {
            String dir_str = rootdir + "src/main/resources" + _baseUri;
            dir = new File(URI.create(dir_str));
            if (!dir.exists()) {
                dir_str = rootdir + "src/main/webapp" + _baseUri;
                dir = new File(URI.create(dir_str));
            }
        }

        try {
            if (dir != null && dir.exists()) {
                provider_debug.setDevMode(true);
                provider_debug.setBaseTemplatePath(dir.getPath());
                provider_debug.setSourceFactory(new FileSourceFactory());
            }

            //通过事件扩展
            EventBus.push(provider_debug);
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    private void forRelease() {
        if(provider != null){
            return;
        }

        provider = Engine.use();

        try {
            provider.setBaseTemplatePath(_baseUri);
            provider.setSourceFactory(new ClassPathSourceFactory());

            //通过事件扩展
            EventBus.push(provider);
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    /**
     * 添加共享指令（自定义标签）
     * */
    public void putDirective(String name, Class<? extends Directive> clz) {
        try {
            provider.addDirective(name, clz);

            if(provider_debug != null){
                provider_debug.addDirective(name, clz);
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
            provider.addSharedObject(name, value);

            if(provider_debug != null){
                provider_debug.addSharedObject(name, value);
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
            ctx.headerSet("Solon-View", "EnjoyRender");
        }

        Template template = null;

        if(provider_debug != null) {
            if (provider_debug.getEngineConfig().getDirective(mv.view()) != null) {
                template = provider_debug.getTemplate(mv.view());
            }
        }

        if(template == null) {
            template = provider.getTemplate(mv.view());
        }

        PrintWriter writer = new PrintWriter(outputStream.get());
        template.render(mv.model(), writer);
        writer.flush();
    }
}
