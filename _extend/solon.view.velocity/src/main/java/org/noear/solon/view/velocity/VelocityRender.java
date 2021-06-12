package org.noear.solon.view.velocity;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.directive.Directive;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.handle.Context;
import org.noear.solon.ext.SupplierEx;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class VelocityRender implements Render {
    private static VelocityRender _global;

    public static VelocityRender global() {
        if (_global == null) {
            _global = new VelocityRender();
        }

        return _global;
    }

    private RuntimeInstance engine;
    private RuntimeInstance engine_debug;
    private Map<String, Object> _sharedVariable = new HashMap<>();

    private String _baseUri = "/WEB-INF/view/";

    //不要要入参，方便后面多视图混用
    //
    public VelocityRender() {
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

        engineInit(engine);
        engineInit(engine_debug);

        Solon.global().onSharedAdd((k, v) -> {
            putVariable(k, v);
        });
    }

    private void engineInit(RuntimeInstance ve) {
        if(ve == null){
            return;
        }

        ve.setProperty(Velocity.ENCODING_DEFAULT, getEncoding());
        ve.setProperty(Velocity.INPUT_ENCODING, getEncoding());

        Solon.cfg().forEach((k, v) -> {
            String key = k.toString();
            if (key.startsWith("velocity")) {
                ve.setProperty(key, v);
            }
        });

        ve.init();
    }

    private void forDebug() {
        if (engine_debug != null) {
            return;
        }

        engine_debug = new RuntimeInstance();

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
                engine_debug.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, dir.getAbsolutePath() + File.separatorChar);
            } else {
                //如果没有找到文件，则使用发行模式
                //
                forRelease();
            }
        } catch (Exception ex) {
            EventBus.push(ex);
        }
    }

    private void forRelease() {
        if (engine != null) {
            return;
        }

        engine = new RuntimeInstance();

        String root_path = Utils.getResource(_baseUri).getPath();

        engine.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, true);
        engine.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, root_path);
    }

    /**
     * 添加共享指令（自定义标签）
     * */
    public <T extends Directive> void putDirective(String name, T obj) {
        engine.addDirective(obj);

        if (engine_debug != null) {
            engine_debug.addDirective(obj);
        }
    }

    /**
     * 添加共享变量
     * */
    public void putVariable(String key, Object obj) {
        _sharedVariable.put(key, obj);
    }

    public String getEncoding() {
        return "utf-8";
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
            ctx.headerSet("solon.view", "VelocityRender");
        }

        String view = mv.view();

        //取得velocity的模版
        Template template = null;

        if (engine_debug != null) {
            try {
                template = engine_debug.getTemplate(view, getEncoding());
            } catch (ResourceNotFoundException ex) {
                //忽略此异常
            }
        }

        if (template == null) {
            template = engine.getTemplate(view, getEncoding());
        }

        // 取得velocity的上下文context
        VelocityContext vc = new VelocityContext(mv.model());
        _sharedVariable.forEach((k, v) -> vc.put(k, v));

        // 输出流
        PrintWriter writer = new PrintWriter(outputStream.get());
        // 转换输出
        template.merge(vc, writer);
        writer.flush();
    }
}
