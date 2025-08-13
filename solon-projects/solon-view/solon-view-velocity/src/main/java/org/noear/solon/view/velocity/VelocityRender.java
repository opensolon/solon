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
package org.noear.solon.view.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.directive.Directive;
import org.noear.solon.Solon;
import org.noear.solon.server.ServerProps;
import org.noear.solon.server.util.DebugUtils;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.core.util.SupplierEx;
import org.noear.solon.view.ViewConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Velocity 视图渲染器
 *
 * @author noear
 * @since 1.0
 * */
public class VelocityRender implements Render {
    static final Logger log = LoggerFactory.getLogger(VelocityRender.class);

    private final ClassLoader classLoader;
    private final String viewPrefix;

    private Map<String, Object> sharedVariables = new HashMap<>();
    private RuntimeInstance provider;
    private RuntimeInstance providerOfDebug;
    /**
     * 引擎提供者
     * */
    public RuntimeInstance getProvider() {
        return provider;
    }
    /**
     * 引擎提供者（调试模式）
     * */
    public RuntimeInstance getProviderOfDebug() {
        return providerOfDebug;
    }

    //不要要入参，方便后面多视图混用
    //
    public VelocityRender() {
        this(AppClassLoader.global(), null);
    }

    public VelocityRender(ClassLoader classLoader) {
        this(classLoader, null);
    }

    public VelocityRender(ClassLoader classLoader, String viewPrefix) {
        this.classLoader = classLoader;
        if(viewPrefix == null){
            this.viewPrefix = ViewConfig.getViewPrefix();
        }else {
            this.viewPrefix = viewPrefix;
        }

        forDebug();
        forRelease();

        engineInit(provider);
        engineInit(providerOfDebug);
    }

    private void engineInit(RuntimeInstance ve) {
        if (ve == null) {
            return;
        }

        ve.setProperty(Velocity.ENCODING_DEFAULT, Solon.encoding());
        ve.setProperty(Velocity.INPUT_ENCODING, Solon.encoding());

        Solon.cfg().forEach((k, v) -> {
            String key = k.toString();
            if (key.startsWith("velocity")) {
                ve.setProperty(key, v);
            }
        });

        ve.init();
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

        if(dir == null){
            return;
        }

        providerOfDebug = new RuntimeInstance();

        try {
            if (dir.exists()) {
                providerOfDebug.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, dir.getPath() + File.separatorChar);
            } else {
                //如果没有找到文件，则使用发行模式
                //
                forRelease();
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    private void forRelease() {
        if (provider != null) {
            return;
        }

        provider = new RuntimeInstance();

        if (ResourceUtil.hasFile(viewPrefix)) {
            //file:...
            URL dir = ResourceUtil.findResource(classLoader, viewPrefix, false);
            provider.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, dir.getFile() + File.separatorChar);
        } else {
            URL resource = ResourceUtil.getResource(classLoader, viewPrefix);
            if (resource == null) {
                return;
            }

            String root_path = resource.getPath();

            provider.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, true);
            provider.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, root_path);
        }
    }

    /**
     * 添加共享指令（自定义标签）
     */
    public <T extends Directive> void putDirective(T obj) {
        provider.addDirective(obj);

        if (providerOfDebug != null) {
            providerOfDebug.addDirective(obj);
        }
    }

    /**
     * 添加共享变量
     */
    public void putVariable(String key, Object obj) {
        sharedVariables.put(key, obj);
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
            ctx.headerSet(ViewConfig.HEADER_VIEW_META, "VelocityRender");
        }

        //添加 context 变量
        mv.putIfAbsent("context", ctx);

        String view = mv.view();

        //取得velocity的模版
        Template template = null;

        if (providerOfDebug != null) {
            try {
                template = providerOfDebug.getTemplate(view, Solon.encoding());
            } catch (ResourceNotFoundException ex) {
                //忽略不计
            }
        }

        if (template == null) {
            template = provider.getTemplate(view, Solon.encoding());
        }

        // 取得velocity的上下文context
        VelocityContext vc = new VelocityContext(mv.model());
        sharedVariables.forEach((k, v) -> vc.put(k, v));

        // 输出流
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream.get(), ServerProps.response_encoding));
        template.merge(vc, writer);
        writer.flush();
    }
}
