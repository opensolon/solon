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
package org.noear.solon.view.beetl;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.FileResourceLoader;
import org.beetl.core.statement.ErrorGrammarProgram;
import org.beetl.core.tag.Tag;
import org.beetl.core.tag.TagFactory;
import org.noear.solon.Solon;
import org.noear.solon.boot.web.DebugUtils;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.core.util.SupplierEx;
import org.noear.solon.view.ViewConfig;
import org.noear.solon.boot.ServerProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;

/**
 * Beetl 视图渲染器
 *
 * @author noear
 * @since 1.0
 * */
public class BeetlRender implements Render {
    static final Logger log = LoggerFactory.getLogger(BeetlRender.class);

    private final ClassLoader classLoader;
    private final String viewPrefix;

    private Configuration config = null;

    private GroupTemplate provider = null;
    private GroupTemplate providerOfDebug = null;

    /**
     * 引擎提供者
     */
    public GroupTemplate getProvider() {
        return provider;
    }

    /**
     * 引擎提供者（调试模式）
     */
    public GroupTemplate getProviderOfDebug() {
        return providerOfDebug;
    }

    /**
     * 获取配置
     */
    public Configuration getConfig() {
        return config;
    }

    //不要要入参，方便后面多视图混用
    //
    public BeetlRender() {
        this(AppClassLoader.global(), null);
    }

    public BeetlRender(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public BeetlRender(ClassLoader classLoader, String viewPrefix) {
        this.classLoader = classLoader;
        if (viewPrefix == null) {
            this.viewPrefix = ViewConfig.getViewPrefix();
        } else {
            this.viewPrefix = viewPrefix;
        }

        try {
            config = Configuration.defaultConfiguration();
            config.setCharset(ServerProps.response_encoding);
            config.setNativeCall(true);
            config.setNativeSecurity("org.beetl.core.DefaultNativeSecurityManager");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

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

        if (ResourceUtil.hasFile(viewPrefix)) {
            return;
        }

        if (providerOfDebug != null) {
            return;
        }

        //添加调试模式
        File dir = DebugUtils.getDebugLocation(classLoader, viewPrefix);

        if (dir == null) {
            return;
        }

        try {
            if (dir.exists()) {
                FileResourceLoader loader = new FileResourceLoader(dir.getPath(), Solon.encoding());
                loader.setAutoCheck(true);
                providerOfDebug = new GroupTemplate(loader, config);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void forRelease() {
        if (provider != null) {
            return;
        }

        try {
            if (ResourceUtil.hasFile(viewPrefix)) {
                //file:...
                URL dir = ResourceUtil.findResource(classLoader, viewPrefix, false);
                FileResourceLoader loader = new FileResourceLoader(dir.getFile(), Solon.encoding());
                provider = new GroupTemplate(loader, config);
            } else {
                ClasspathResourceLoader loader = new ClasspathResourceLoader(classLoader, viewPrefix);
                provider = new GroupTemplate(loader, config);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 添加共享指令（自定义标签）
     */
    public void putDirective(String name, Class<? extends Tag> clz) {
        try {
            provider.registerTag(name, clz);

            if (providerOfDebug != null) {
                providerOfDebug.registerTag(name, clz);
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 添加共享指令（自定义标签）
     */
    public void putDirective(String name, TagFactory tagFactory) {
        try {
            provider.registerTagFactory(name, tagFactory);

            if (providerOfDebug != null) {
                providerOfDebug.registerTagFactory(name, tagFactory);
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
            provider.getSharedVars().put(name, value);

            if (providerOfDebug != null) {
                providerOfDebug.getSharedVars().put(name, value);
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

    private void render_mav(ModelAndView mv, Context ctx, SupplierEx<OutputStream> outputStream) throws Throwable {
        if (ctx.contentTypeNew() == null) {
            ctx.contentType(MimeType.TEXT_HTML_UTF8_VALUE);
        }

        if (ViewConfig.isOutputMeta()) {
            ctx.headerSet(ViewConfig.HEADER_VIEW_META, "BeetlRender");
        }

        //添加 context 变量
        mv.putIfAbsent("context", ctx);

        Template template = null;

        if (providerOfDebug != null) {
            try {
                template = providerOfDebug.getTemplate(mv.view());
                if (template != null && template.program instanceof ErrorGrammarProgram) {
                    template = null;
                }
            } catch (Exception e) {
                //忽略不计
            }
        }

        if (template == null) {
            template = provider.getTemplate(mv.view());
        }


        template.binding(mv.model());
        template.renderTo(outputStream.get());
    }
}
