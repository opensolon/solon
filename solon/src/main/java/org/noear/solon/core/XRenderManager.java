package org.noear.solon.core;

import org.noear.solon.XUtil;
import org.noear.solon.ext.PrintUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过 XRender 管理员，以此实现多模板引擎处理
 * */
public class XRenderManager implements XRender {

    public static final String mapping_model = "model";
    private static final Map<String, XRender> _mapping = new HashMap<>();
    private static final Map<String, XRender> _lib = new HashMap<>();

    private static XRender _def = (d, c) -> {
        if (d != null) {
            c.output(d.toString());
        }
    };

    private XRenderManager() {
        mapping(mapping_model, _def);
    }

    //不能放上面
    public static XRenderManager global = new XRenderManager();

    /**
     * 登记渲染器
     */
    public static void register(XRender render) {
        _def = render;
        _lib.put(render.getClass().getSimpleName(), render);
        _lib.put(render.getClass().getName(), render);

        PrintUtil.blueln("solon:: view load:" + render.getClass().getSimpleName());
        PrintUtil.blueln("solon:: view load:" + render.getClass().getName());
    }

    /**
     * 印射后缀和渲染器的关系
     *
     * @param suffix = .ftl
     */
    public static void mapping(String suffix, XRender render) {
        //suffix=.ftl
        _mapping.put(suffix, render);

        PrintUtil.blueln("solon:: view mapping: " + suffix + "=" + render.getClass().getSimpleName());
    }

    /**
     * 印射后缀和渲染器的关系
     *
     * @param suffix = .ftl
     */
    public static void mapping(String suffix, String className) {
        XRender render = _lib.get(className);
        if (render == null) {
            PrintUtil.redln("solon:: " + className + " not exists!");
            return;
            //throw new RuntimeException(classSimpleName + " not exists!");
        }

        _mapping.put(suffix, render);

        PrintUtil.blueln("solon:: view mapping: " + suffix + "=" + className);
    }

    /**
     * 执行渲染
     */
    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        if (obj instanceof ModelAndView) {
            ModelAndView mv = (ModelAndView) obj;

            if (XUtil.isEmpty(mv.view()) == false) {
                //
                //如果有视图
                //
                int suffix_idx = mv.view().lastIndexOf(".");
                if (suffix_idx > 0) {
                    String suffix = mv.view().substring(suffix_idx);
                    XRender render = _mapping.get(suffix);

                    if (render != null) {
                        //如果找到对应的渲染器
                        //
                        render.render(mv, ctx);
                        return;
                    }
                }

                //如果没有则用默认渲染器
                //
                _def.render(mv, ctx);
                return;
            }
        }

        //string 则直接输出（前面可能已指定contentType）
        if (obj instanceof String) {
            ctx.output((String) obj);
            return;
        }

        //最后只有 model
        //
        _mapping.get(mapping_model).render(obj, ctx);
    }
}
