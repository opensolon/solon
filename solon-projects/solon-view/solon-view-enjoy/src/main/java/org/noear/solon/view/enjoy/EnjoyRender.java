/*
 * Copyright 2017-2025 noear.org and authors
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
package org.noear.solon.view.enjoy;

import com.jfinal.template.Directive;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.jfinal.template.source.FileSourceFactory;
import org.noear.solon.Solon;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.web.DebugUtils;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.core.util.SupplierEx;
import org.noear.solon.view.ViewConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

/**
 * Enjoy 视图渲染器
 *
 * @author noear
 * @since 1.0
 * */
public class EnjoyRender implements Render {
    static final Logger log = LoggerFactory.getLogger(EnjoyRender.class);

    private final ClassLoader classLoader;
    private final String viewPrefix;

    private Engine provider = null;
    private Engine providerOfDebug = null;

    /**
     * 引擎提供者
     * */
    public Engine getProvider() {
        return provider;
    }

    /**
     * 引擎提供者（调试模式）
     * */
    public Engine getProviderOfDebug() {
        return providerOfDebug;
    }

    //不要要入参，方便后面多视图混用
    //
    public EnjoyRender() {
        this(AppClassLoader.global(), null);
    }

    public EnjoyRender(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public EnjoyRender(ClassLoader classLoader, String viewPrefix) {
        this.classLoader = classLoader;
        if(viewPrefix == null){
            this.viewPrefix = ViewConfig.getViewPrefix();
        }else {
            this.viewPrefix = viewPrefix;
        }

        //开始中文支持
        Engine.setChineseExpression(true);

        forDebug();
        forRelease();
    }


    private void forDebug() {
        if (Solon.cfg().isDebugMode() == false) {
            return;
        }

        if (Solon.cfg().isFilesMode() == false) {
            return;
        }

        if (providerOfDebug != null) {
            return;
        }

        //添加调试模式
        File dir = DebugUtils.getDebugLocation(classLoader, viewPrefix);

        if(dir == null){
            return;
        }

        providerOfDebug = Engine.create("debug");
        providerOfDebug.setDevMode(true);

        try {
            if (dir.exists()) {
                providerOfDebug.setBaseTemplatePath(dir.getPath());
                providerOfDebug.setSourceFactory(new FileSourceFactory());
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void forRelease() {
        if (provider != null) {
            return;
        }

        provider = Engine.use();
        provider.setDevMode(Solon.cfg().isDebugMode());

        try {
            if (ResourceUtil.hasFile(viewPrefix)) {
                //file:...
                URL dir = ResourceUtil.findResource(classLoader, viewPrefix, false);
                provider.setBaseTemplatePath(dir.getFile());
                provider.setSourceFactory(new FileSourceFactory());
            }else {
                provider.setBaseTemplatePath(viewPrefix);
                provider.setSourceFactory(new ClassPathSourceFactory2(classLoader));
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 添加共享指令（自定义标签）
     */
    public void putDirective(String name, Class<? extends Directive> clz) {
        try {
            provider.getEngineConfig().addDirective(name, clz);

            if (providerOfDebug != null) {
                providerOfDebug.getEngineConfig().addDirective(name, clz);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 添加共享指令（自定义标签）
     */
    public void putDirective(String name, EnjoyDirectiveFactory directiveFactory) {
        try {
            provider.getEngineConfig().addDirective(name, directiveFactory);

            if (providerOfDebug != null) {
                providerOfDebug.getEngineConfig().addDirective(name, directiveFactory);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 添加共享变量
     */
    public void putVariable(String name, Object value) {
        try {
            provider.addSharedObject(name, value);

            if (providerOfDebug != null) {
                providerOfDebug.addSharedObject(name, value);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 添加共享模板函数
     */
    public void putFunction(String path) {
        try {
            provider.addSharedFunction(path);

            if (providerOfDebug != null) {
                providerOfDebug.addSharedFunction(path);
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
            ctx.headerSet(ViewConfig.HEADER_VIEW_META, "EnjoyRender");
        }

        //添加 context 变量
        mv.putIfAbsent("context", ctx);

        Template template = null;

        if (providerOfDebug != null) {
            try {
                template = providerOfDebug.getTemplate(mv.view());
            } catch (Exception e) {
                //忽略不计
            }
        }

        if (template == null) {
            template = provider.getTemplate(mv.view());
        }

        // 输出流
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream.get(), ServerProps.response_encoding));
        template.render(mv.model(), writer);
        writer.flush();
    }
}
