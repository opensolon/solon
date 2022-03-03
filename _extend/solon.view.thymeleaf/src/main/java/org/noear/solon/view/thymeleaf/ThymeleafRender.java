package org.noear.solon.view.thymeleaf;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.ext.SupplierEx;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ThymeleafRender implements Render {
    private static ThymeleafRender _global;

    public static ThymeleafRender global() {
        if (_global == null) {
            _global = new ThymeleafRender();
        }

        return _global;
    }


    private TemplateEngine provider = new TemplateEngine();

    private Map<String, Object> _sharedVariable = new HashMap<>();

    private String _baseUri = "/WEB-INF/view/";

    public ThymeleafRender() {
        String baseUri = Solon.cfg().get("slon.mvc.view.prefix");

        if (Utils.isEmpty(baseUri) == false) {
            _baseUri = baseUri;
        }

        forDebug();
        forRelease();

        try {
            Solon.global().shared().forEach((k, v) -> {
                putVariable(k, v);
            });

        } catch (Exception ex) {
            EventBus.push(ex);
        }

        Solon.global().onSharedAdd((k, v) -> {
            putVariable(k, v);
        });
    }

    private void forDebug() {
        if(Solon.cfg().isDebugMode() == false) {
            return;
        }

        //添加调试模式
        URL rooturi = Utils.getResource("/");
        if (rooturi == null) {
            return;
        }

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
                FileTemplateResolver _loader = new FileTemplateResolver();
                _loader.setPrefix(dir.getAbsolutePath() + File.separatorChar);
                _loader.setTemplateMode(TemplateMode.HTML);
                _loader.setCacheable(false);//必须为false
                _loader.setCharacterEncoding("utf-8");
                _loader.setCacheTTLMs(Long.valueOf(3600000L));

                provider.addTemplateResolver(_loader);
            }

            //通过事件扩展
            EventBus.push(provider);
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    private void forRelease() {
        ClassLoaderTemplateResolver _loader = new ClassLoaderTemplateResolver(JarClassLoader.global());

        _loader.setPrefix(_baseUri);
        _loader.setTemplateMode(TemplateMode.HTML);
        _loader.setCacheable(true);
        _loader.setCharacterEncoding("utf-8");
        _loader.setCacheTTLMs(Long.valueOf(3600000L));

        provider.addTemplateResolver(_loader);

        //通过事件扩展
        EventBus.push(provider);
    }


    /**
     * 添加共享指令（自定义标签）
     */
    public <T extends IDialect> void putDirective(T obj) {
        try {
            provider.addDialect(obj);
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    /**
     * 添加共享变量
     */
    public void putVariable(String name, Object obj) {
        _sharedVariable.put(name, obj);
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
            ctx.headerSet("Solon-View", "ThymeleafRender");
        }

        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
        context.setVariables(_sharedVariable);
        context.setVariables(mv);

        if (ctx.getLocale() != null) {
            context.setLocale(ctx.getLocale());
        }


        PrintWriter writer = new PrintWriter(outputStream.get());

        provider.process(mv.view(), context, writer);

        writer.flush();
    }
}
