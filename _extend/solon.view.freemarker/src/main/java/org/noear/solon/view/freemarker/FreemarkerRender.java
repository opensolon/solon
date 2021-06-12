package org.noear.solon.view.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateNotFoundException;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.ext.SupplierEx;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;

public class FreemarkerRender implements Render {
    private static FreemarkerRender _global;

    public static FreemarkerRender global() {
        if (_global == null) {
            _global = new FreemarkerRender();
        }

        return _global;
    }


    Configuration cfg;
    Configuration cfg_debug;

    private String _baseUri = "/WEB-INF/view/";

    //不要要入参，方便后面多视图混用
    //
    public FreemarkerRender() {
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

    //尝试 调试模式 进行实始化
    private void forDebug() {
        if (cfg_debug != null) {
            return;
        }

        cfg_debug = new Configuration(Configuration.VERSION_2_3_28);
        cfg_debug.setNumberFormat("#");
        cfg_debug.setDefaultEncoding("utf-8");

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
                cfg_debug.setDirectoryForTemplateLoading(dir);
            } else {
                //如果没有找到文件，则使用发行模式
                //
                forRelease();
            }
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    //使用 发布模式 进行实始化
    private void forRelease() {
        if (cfg != null) {
            return;
        }

        cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setNumberFormat("#");
        cfg.setDefaultEncoding("utf-8");

        try {
            cfg.setClassLoaderForTemplateLoading(JarClassLoader.global(), _baseUri);
        } catch (Exception ex) {
            EventBus.push(ex);
        }

        cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(0, Integer.MAX_VALUE));
    }

    /**
     * 添加共享指令（自定义标签）
     * */
    public <T extends TemplateDirectiveModel> void putDirective(String name, T obj) {
        putVariable(name, obj);
    }

    /**
     * 添加共享变量
     * */
    public void putVariable(String name, Object value) {
        try {
            cfg.setSharedVariable(name, value);

            if (cfg_debug != null) {
                cfg_debug.setSharedVariable(name, value);
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
            ctx.headerSet("solon.view", "FreemarkerRender");
        }

        PrintWriter writer = new PrintWriter(outputStream.get());

        Template template = null;

        if (cfg_debug != null) {
            try {
                template = cfg_debug.getTemplate(mv.view(), "utf-8");
            } catch (TemplateNotFoundException ex) {
                //跳过
            }
        }

        if (template == null) {
            template = cfg.getTemplate(mv.view(), "utf-8");
        }

        template.process(mv.model(), writer);
    }
}
