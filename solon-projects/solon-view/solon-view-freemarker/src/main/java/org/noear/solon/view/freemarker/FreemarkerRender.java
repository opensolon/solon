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
package org.noear.solon.view.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateNotFoundException;
import org.noear.solon.Solon;
import org.noear.solon.boot.ServerProps;
import org.noear.solon.boot.web.DebugUtils;
import org.noear.solon.core.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.core.util.SupplierEx;
import org.noear.solon.view.ViewConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

public class FreemarkerRender implements Render {
    static final Logger log = LoggerFactory.getLogger(FreemarkerRender.class);

    private final ClassLoader classLoader;
    private final String viewPrefix;

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
        this(AppClassLoader.global(), null);
    }

    public FreemarkerRender(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public FreemarkerRender(ClassLoader classLoader, String viewPrefix) {
        this.classLoader = classLoader;
        if(viewPrefix == null){
            this.viewPrefix = ViewConfig.getViewPrefix();
        }else {
            this.viewPrefix = viewPrefix;
        }

        forDebug();
        forRelease();
    }

    //尝试 调试模式 进行实始化
    private void forDebug() {
        if(Solon.cfg().isDebugMode() == false) {
            return;
        }

        if (Solon.cfg().isFilesMode() == false) {
            return;
        }

        if (ResourceUtil.hasFile(viewPrefix)) {
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

        providerOfDebug = new Configuration(Configuration.VERSION_2_3_28);
        providerOfDebug.setNumberFormat("#");
        providerOfDebug.setDefaultEncoding("utf-8");

        try {
            if (dir.exists()) {
                providerOfDebug.setDirectoryForTemplateLoading(dir);
            }
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
            if (ResourceUtil.hasFile(viewPrefix)) {
                //file:...
                URL dir = ResourceUtil.findResource(classLoader, viewPrefix, false);
                provider.setDirectoryForTemplateLoading(new File(dir.getFile()));
            } else {
                provider.setClassLoaderForTemplateLoading(classLoader, viewPrefix);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        provider.setCacheStorage(new freemarker.cache.MruCacheStorage(0, Integer.MAX_VALUE));
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
            ctx.contentType(MimeType.TEXT_HTML_UTF8_VALUE);
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
