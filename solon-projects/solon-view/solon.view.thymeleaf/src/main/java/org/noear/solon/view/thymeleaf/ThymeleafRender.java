/*
 * Copyright 2017-2024 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.view.thymeleaf;

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

/**
 * Thymeleaf 视图渲染器
 *
 * @author noear
 * @since 1.0
 * */
public class ThymeleafRender implements Render {
    static final Logger log = LoggerFactory.getLogger(ThymeleafRender.class);

    private static ThymeleafRender _global;

    public static ThymeleafRender global() {
        if (_global == null) {
            _global = new ThymeleafRender();
        }

        return _global;
    }

    private final ClassLoader classLoader;
    private final String viewPrefix;

    private Map<String, Object> sharedVariables = new HashMap<>();
    private TemplateEngine provider = new TemplateEngine();

    /**
     * 引擎提供者
     */
    public TemplateEngine getProvider() {
        return provider;
    }

    public ThymeleafRender() {
        this(AppClassLoader.global(), null);
    }

    public ThymeleafRender(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public ThymeleafRender(ClassLoader classLoader, String viewPrefix) {
        this.classLoader = classLoader;
        if(viewPrefix == null){
            this.viewPrefix = ViewConfig.getViewPrefix();
        }else {
            this.viewPrefix = viewPrefix;
        }

        forDebug();
        forRelease();

        Solon.app().shared().forEach((k, v) -> {
            putVariable(k, v);
        });

        Solon.app().onSharedAdd((k, v) -> {
            putVariable(k, v);
        });
    }

    private void forDebug() {
        if (Solon.cfg().isDebugMode() == false) {
            return;
        }

        if (Solon.cfg().isFilesMode() == false) {
            return;
        }

        //添加调试模式
        File dir = ViewConfig.getDebugLocation(classLoader, viewPrefix);

        if(dir == null){
            return;
        }

        try {
            if (dir.exists()) {
                FileTemplateResolver _loader = new FileTemplateResolver();
                _loader.setPrefix(dir.getAbsolutePath() + File.separatorChar);
                _loader.setTemplateMode(TemplateMode.HTML);
                _loader.setCacheable(false);//必须为false
                _loader.setCharacterEncoding("utf-8");
                _loader.setCacheTTLMs(Long.valueOf(3600000L));

                provider.addTemplateResolver(_loader);
            }

            //通过事件扩展
            EventBus.publish(provider);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void forRelease() {
        if (ResourceUtil.hasFile(viewPrefix)) {
            //file:...
            URL dir = ResourceUtil.findResource(classLoader, viewPrefix, false);

            FileTemplateResolver _loader = new FileTemplateResolver();
            _loader.setPrefix(dir.getFile() + File.separatorChar);
            _loader.setTemplateMode(TemplateMode.HTML);
            _loader.setCacheable(false);//必须为false
            _loader.setCharacterEncoding("utf-8");
            _loader.setCacheTTLMs(Long.valueOf(3600000L));

            provider.addTemplateResolver(_loader);
        } else {
            ClassLoaderTemplateResolver _loader = new ClassLoaderTemplateResolver(classLoader);

            _loader.setPrefix(viewPrefix);
            _loader.setTemplateMode(TemplateMode.HTML);
            _loader.setCacheable(true);
            _loader.setCharacterEncoding("utf-8");
            _loader.setCacheTTLMs(Long.valueOf(3600000L));

            provider.addTemplateResolver(_loader);
        }

        //通过事件扩展
        EventBus.publish(provider);
    }


    /**
     * 添加共享指令（自定义标签）
     */
    public <T extends IDialect> void putDirective(T obj) {
        try {
            provider.addDialect(obj);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 添加共享变量
     */
    public void putVariable(String name, Object obj) {
        sharedVariables.put(name, obj);
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
            ctx.headerSet(ViewConfig.HEADER_VIEW_META, "ThymeleafRender");
        }

        //添加 context 变量
        mv.putIfAbsent("context", ctx);

        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
        context.setVariables(sharedVariables);
        context.setVariables(mv.model());

        if (ctx.getLocale() != null) {
            context.setLocale(ctx.getLocale());
        }

        // 输出流
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream.get(), ServerProps.response_encoding));
        provider.process(mv.view(), context, writer);
        writer.flush();
    }
}
