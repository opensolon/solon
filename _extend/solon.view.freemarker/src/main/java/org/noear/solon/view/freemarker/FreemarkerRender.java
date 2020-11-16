package org.noear.solon.view.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.noear.solon.SolonApp;
import org.noear.solon.Utils;
import org.noear.solon.core.*;
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


    Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

    private String _baseUri = "/WEB-INF/view/";

    //不要要入参，方便后面多视图混用
    //
    public FreemarkerRender() {
        String baseUri = Solon.global().props().get("slon.mvc.view.prefix");

        if (Utils.isEmpty(baseUri) == false) {
            _baseUri = baseUri;
        }


        if (Solon.cfg().isDebugMode()) {
            forDebug();
        } else {
            forRelease();
        }

        cfg.setNumberFormat("#");
        cfg.setDefaultEncoding("utf-8");

        Solon.global().onSharedAdd((k, v) -> {
            setSharedVariable(k, v);
        });
    }

    //尝试 调试模式 进行实始化
    private void forDebug() {
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
                cfg.setDirectoryForTemplateLoading(dir);
            } else {
                //如果没有找到文件，则使用发行模式
                //
                forRelease();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //使用 发布模式 进行实始化
    private void forRelease() {
        try {
            cfg.setClassLoaderForTemplateLoading(JarClassLoader.global(), _baseUri);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        cfg.setCacheStorage(new freemarker.cache.MruCacheStorage(0, Integer.MAX_VALUE));
    }

    public void setSharedVariable(String name, Object value) {
        try {
            cfg.setSharedVariable(name, value);
        } catch (Exception ex) {
            ex.printStackTrace();
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

        Template template = cfg.getTemplate(mv.view(), "utf-8");

        template.process(mv.model(), writer);
    }
}
