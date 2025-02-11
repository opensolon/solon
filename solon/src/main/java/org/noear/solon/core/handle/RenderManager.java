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
package org.noear.solon.core.handle;

import org.noear.solon.Utils;
import org.noear.solon.core.serialize.Stringable;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.DataThrowable;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过 Render 管理员，以此实现多模板引擎处理
 *
 * @author noear
 * @since 1.0
 * */
public class RenderManager implements Render {
    private final Map<String, Render> _mapping = new HashMap<>();
    private final Map<String, Render> _lib = new HashMap<>();


    //默认渲染器
    private Render _def = (d, c) -> {
        if (d != null) {
            c.output(d.toString());
        }
    };


    /**
     * 获取渲染器
     */
    public Render get(String name) {
        Render tmp = _lib.get(name);
        if (tmp == null) {
            tmp = _mapping.get(name);
        }

        return tmp;
    }

    /**
     * 登记渲染器
     *
     * @param renderFactory 渲染器工厂
     */
    public void register(RenderFactory renderFactory) {
        Render render = renderFactory.create();
        for (String mapping : renderFactory.mappings()) {
            register(mapping, render);
        }
    }

    /**
     * 登记渲染器（并映射关系）
     *
     * @param mapping 映射（例：.ftl, @json）
     * @param render  渲染器
     */
    public void register(String mapping, Render render) {
        if (render == null) {
            return;
        }

        if (Utils.isEmpty(mapping)) {
            //def | class
            _def = render;
            _lib.put(render.getClass().getSimpleName(), render);
            _lib.put(render.getClass().getName(), render);

            LogUtil.global().info("View: load: " + render.getClass().getSimpleName());
            LogUtil.global().info("View: load: " + render.getClass().getName());
        } else {
            //mapping=.ftl | @json
            _mapping.put(mapping, render);

            LogUtil.global().info("Render mapping: " + mapping + "=" + render.name());
        }
    }

    /**
     * 登记渲染器（并映射关系）
     *
     * @param mapping 映射（例：.ftl, @json）
     * @param clzName 渲染器类名
     */
    public void register(String mapping, String clzName) {
        if (mapping == null || clzName == null) {
            return;
        }

        Render render = _lib.get(clzName);
        if (render == null) {
            LogUtil.global().warn("Render: " + clzName + " not exists!");
            return;
            //throw new IllegalStateException(classSimpleName + " not exists!");
        }

        _mapping.put(mapping, render);

        LogUtil.global().info("Render mapping: " + mapping + "=" + clzName);
    }

    /**
     * 渲染并返回
     */
    public String renderAndReturn(ModelAndView modelAndView) {
        try {
            return renderAndReturn(modelAndView, Context.current());
        } catch (Throwable ex) {
            ex = Utils.throwableUnwrap(ex);
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            } else {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 渲染并返回
     */
    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        if (data instanceof ModelAndView) {
            ModelAndView mv = (ModelAndView) data;

            if (Utils.isNotEmpty(mv.view())) {
                //
                //如果有视图
                //
                int suffix_idx = mv.view().lastIndexOf(".");
                if (suffix_idx > 0) {
                    String suffix = mv.view().substring(suffix_idx);
                    Render render = _mapping.get(suffix);

                    if (render != null) {
                        //如果找到对应的渲染器
                        //
                        //同步上下文特性
                        ctx.attrMap().forEach((k, v) -> {
                            mv.putIfAbsent(k, v);
                        });
                        //视图渲染
                        return render.renderAndReturn(mv, ctx);
                    }
                }

                //如果没有则用默认渲染器
                //
                return _def.renderAndReturn(mv, ctx);
            } else {
                data = mv.model();
            }
        }

        Render render = resolveRander(ctx);

        if (render != null) {
            return render.renderAndReturn(data, ctx);
        } else {
            //最后只有 def
            //
            return _def.renderAndReturn(data, ctx);
        }
    }


    /**
     * 渲染
     *
     * @param data 数据
     * @param ctx  上下文
     */
    @Override
    public void render(Object data, Context ctx) throws Throwable {
        if (data instanceof DataThrowable) {
            return;
        }

        //可字符串化的？转为字符串
        if(data instanceof Stringable){
            data = data.toString();
        }

        //如果是模型视图
        if (data instanceof ModelAndView) {
            ModelAndView mv = (ModelAndView) data;

            if (Utils.isEmpty(mv.view()) == false) {
                //
                //如果有视图
                //
                int suffix_idx = mv.view().lastIndexOf(".");
                if (suffix_idx > 0) {
                    String suffix = mv.view().substring(suffix_idx);
                    Render render = _mapping.get(suffix);

                    if (render != null) {
                        //如果找到对应的渲染器
                        //
                        //同步上下文特性
                        ctx.attrMap().forEach((k, v) -> {
                            mv.putIfAbsent(k, v);
                        });
                        //视图渲染
                        render.render(mv, ctx);
                        return;
                    }
                }

                //如果没有则用默认渲染器
                //
                _def.render(mv, ctx);
                return;
            } else {
                data = mv.model();
            }
        }

        if (data instanceof File) {
            ctx.outputAsFile((File) data);
            return;
        }

        //如果是文件
        if (data instanceof DownloadedFile) {
            ctx.outputAsFile((DownloadedFile) data);
            return;
        }

        if (data instanceof InputStream) {
            ctx.output((InputStream) data);
            return;
        }

        Render render = resolveRander(ctx);

        if (render != null) {
            render.render(data, ctx);
        } else {
            //最后只有 def
            //
            _def.render(data, ctx);
        }
    }

    /**
     * 分析出渲染器
     *
     * @since 1.6
     */
    private Render resolveRander(Context ctx) {
        //@json
        //@type_json
        //@xml
        //@protobuf
        //@hessian
        //
        Render render = null;
        String mode = ctx.header("X-Serialization");

        if (Utils.isEmpty(mode)) {
            mode = ctx.attr("@render");
        }

        if (Utils.isEmpty(mode) == false) {
            render = _mapping.get(mode);

            if (render == null) {
                ctx.headerSet("Solon.serialization.mode", "Not supported " + mode);
            }
        }

        if (render == null) {
            //根据接收类型匹配
            String at = ctx.acceptNew();
            for (Render r : _mapping.values()) {
                if (r.matched(ctx, at)) {
                    render = r;
                    break;
                }
            }
        }

        if (render == null) {
            if (ctx.remoting()) {
                render = _mapping.get("@type_json");
            } else {
                render = _mapping.get("@json");
            }
        }

        return render;
    }
}