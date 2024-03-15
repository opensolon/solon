package org.noear.solon.view.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateNotFoundException;
import org.noear.solon.Solon;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.core.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.core.util.SupplierEx;
import org.noear.solon.view.ViewConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URL;

public class FreemarkerRender implements Render {
    static final Logger log = LoggerFactory.getLogger(FreemarkerRender.class);

    private static FreemarkerRender _global;

    public static FreemarkerRender global() {
        if (_global == null) {
            _global = new FreemarkerRender();
        }

        return _global;
    }

    private ClassLoader classLoader;

    private Configuration provider;
    private Configuration providerOfDebug;

    /**
     * 引擎提供者
     * */
    public Configuration getProvider() {
        return provider;
    }

    /**
     * 引擎提供者（调试模式）
     * */
    public Configuration getProviderOfDebug() {
        return providerOfDebug;
    }

    //不要要入参，方便后面多视图混用
    //
    public FreemarkerRender() {
        this(AppClassLoader.global());
    }
    public FreemarkerRender(ClassLoader classLoader) {
        this.classLoader = classLoader;

        forDebug();
        forRelease();

        Solon.app().shared().forEach((k, v) -> {
            putVariable(k, v);
        });

        Solon.app().onSharedAdd((k, v) -> {
            putVariable(k, v);
        });
    }

    //尝试 调试模式 进行实始化
    private void forDebug() {
        if(Solon.cfg().isDebugMode() == false) {
            return;
        }

        if (Solon.cfg().isFilesMode() == false) {
            return;
        }

        if (providerOfDebug != null) {
            return;
        }

        //添加调试模式
        URL rooturi = ResourceUtil.getResource("/");
        if(rooturi == null){
            return;
        }

        providerOfDebug = new Configuration(Configuration.VERSION_2_3_28);
        providerOfDebug.setNumberFormat("#");
        providerOfDebug.setDefaultEncoding("utf-8");

        String rootdir = rooturi.toString().replace("target/classes/", "");
        File dir = null;

        if (rootdir.startsWith("file:")) {
            String dir_str = rootdir + "src/main/resources" + ViewConfig.getViewPrefix();
            dir = new File(URI.create(dir_str));
            if (!dir.exists()) {
                dir_str = rootdir + "src/main/webapp" + ViewConfig.getViewPrefix();
                dir = new File(URI.create(dir_str));
            }
        }

        try {
            if (dir != null && dir.exists()) {
                providerOfDebug.setDirectoryForTemplateLoading(dir);
            }

            //通过事件扩展
            EventBus.publish(providerOfDebug);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    //使用 发布模式 进行实始化
    private void forRelease() {
        if (provider != null) {
            return;
        }

        provider = new Configuration(Configuration.VERSION_2_3_28);
        provider.setNumberFormat("#");
        provider.setDefaultEncoding("utf-8");

        try {
            provider.setClassLoaderForTemplateLoading(classLoader, ViewConfig.getViewPrefix());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        provider.setCacheStorage(new freemarker.cache.MruCacheStorage(0, Integer.MAX_VALUE));

        //通过事件扩展
        EventBus.publish(provider);
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
            provider.setSharedVariable(name, value);

            if (providerOfDebug != null) {
                providerOfDebug.setSharedVariable(name, value);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
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

        if (ViewConfig.isOutputMeta()) {
            ctx.headerSet(ViewConfig.HEADER_VIEW_META, "FreemarkerRender");
        }

        //添加 context 变量
        mv.putIfAbsent("context", ctx);


        Template template = null;

        if (providerOfDebug != null) {
            try {
                template = providerOfDebug.getTemplate(mv.view(), Solon.encoding());
            } catch (TemplateNotFoundException e) {
                //忽略不计
            }
        }

        if (template == null) {
            template = provider.getTemplate(mv.view(), Solon.encoding());
        }

        // 输出流
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream.get(), ServerProps.response_encoding));
        template.process(mv.model(), writer);
        writer.flush();
    }
}
