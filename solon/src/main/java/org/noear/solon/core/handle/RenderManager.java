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
import org.noear.solon.core.Constants;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.DataThrowable;
import org.noear.solon.core.util.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 通过 Render 管理员，以此实现多模板引擎处理
 *
 * @author noear
 * @since 1.0
 * */
public class RenderManager implements Render {
    static final Logger log = LoggerFactory.getLogger(RenderManager.class);

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

    public void register(Render render) {
        String[] mappings = render.mappings();

        if (mappings != null) {
            for (String mapping : mappings) {
                register(mapping, render);
            }
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

            log.debug("View: load: " + render.getClass().getSimpleName());
            log.debug("View: load: " + render.getClass().getName());
        } else {
            //mapping=.ftl | @json
            _mapping.put(mapping, render);

            log.debug("Render mapping: " + mapping + "=" + render.name());
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

        if (render == null && clzName.indexOf('.') > 0) {
            //如果是全类名
            render = ClassUtil.tryInstance(clzName);
        }

        if (render == null) {
            log.warn("Render: " + clzName + " not found!");
            return;
        }

        _mapping.put(mapping, render);

        log.debug("Render mapping: " + mapping + "=" + clzName);
    }

    /**
     * 渲染并返回
     *
     * @deprecated 3.6 {@link org.noear.solon.core.serialize.Serializer#serialize(Object)}
     */
    @Deprecated
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
     *
     * @deprecated 3.6 {@link org.noear.solon.core.serialize.Serializer#serialize(Object)}
     */
    @Deprecated
    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        //如果是实体
        if (data instanceof Entity) {
            data = ((Entity) data).body();
        }

        if (data instanceof ModelAndView) {
            ModelAndView mv = (ModelAndView) data;

            if (Utils.isNotEmpty(mv.view())) {
                return getViewRender(ctx, mv).renderAndReturn(mv, ctx);
            } else {
                data = mv.model();
            }
        }

        Render render = resolveRander(ctx);

        if (render != null) {
            //明确有渲染器
            return render.renderAndReturn(data, ctx);
        } else {
            //如果未明确？
            if (data instanceof String) {
                return ((String) data);
            } else {
                if (render == null) {
                    render = _mapping.get(Constants.AT_JSON);
                }

                if (render != null) {
                    return render.renderAndReturn(data, ctx);
                } else {
                    //最后只有 def
                    //
                    return _def.renderAndReturn(data, ctx);
                }
            }
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

        //如果是实体
        if (data instanceof Entity) {
            Entity entity = (Entity) data;
            data = entity.body();

            if (ctx.isHeadersSent() == false) {
                if (entity.status() > 0) {
                    ctx.status(entity.status());
                }

                if (entity.headers().isEmpty() == false) {
                    for (KeyValues<String> kv : entity.headers()) {
                        if (Utils.isNotEmpty(kv.getValues())) {
                            if (kv.getValues().size() > 1) {
                                //多个
                                for (String val : kv.getValues()) {
                                    ctx.headerAdd(kv.getKey(), val);
                                }
                            } else {
                                //单个
                                ctx.headerSet(kv.getKey(), kv.getFirstValue());
                            }
                        }
                    }
                }
            }
        }

        //如果是模型视图
        if (data instanceof ModelAndView) {
            ModelAndView mv = (ModelAndView) data;

            if (Utils.isNotEmpty(mv.view())) {
                getViewRender(ctx, mv).render(mv, ctx);
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
            //明确有渲染器
            render.render(data, ctx);
        } else {
            //如果未明确？
            if (data instanceof String) {
                ctx.output((String) data);
            } else {
                if (render == null) {
                    render = _mapping.get(Constants.AT_JSON);
                }

                if (render != null) {
                    render.render(data, ctx);
                } else {
                    //最后只有 def
                    //
                    _def.render(data, ctx);
                }
            }
        }
    }

    private Render getViewRender(Context ctx, ModelAndView mv) {
        if (mv.view().contains("../") || mv.view().contains("..\\")) {
            // '../','..\' 不安全
            throw new IllegalStateException("Invalid view path: '" + mv.view() + "'");
        }

        //查找渲染器
        //
        int suffix_idx = mv.view().lastIndexOf(".");
        if (suffix_idx > 0) {
            String suffix = mv.view().substring(suffix_idx);
            Render render = _mapping.get(suffix);

            if (render != null) {
                //如果找到对应的渲染器
                //
                //同步上下文特性
                for (Map.Entry<String, Object> kv : ctx.attrMap().entrySet()) {
                    mv.putIfAbsent(kv.getKey(), kv.getValue());
                }
                //视图渲染
                return render;
            }
        }

        //如果没有则用默认渲染器
        //
        return _def;
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
            mode = ctx.attr(Constants.ATTR_RENDER);
        }

        if (Utils.isEmpty(mode) == false) {
            render = _mapping.get(mode);

            if (render == null) {
                ctx.headerSet("Solon.serialization.mode", "Not supported " + mode);
            }
        }

        if (render == null) {
            //根据内容类型匹配
            String mime1 = ctx.contentTypeNew();
            if (Utils.isNotEmpty(mime1) && mime1.startsWith(MimeType.TEXT_PLAIN_VALUE) == false) {
                for (Render r : _mapping.values()) {
                    if (r.matched(ctx, mime1)) {
                        render = r;
                        break;
                    }
                }
            }

            if (render == null) {
                //如果没有，根据接收类型匹配
                String mime2 = ctx.acceptNew();
                if (Utils.isNotEmpty(mime2) && Objects.equals(mime2, mime1) == false) {
                    for (Render r : _mapping.values()) {
                        if (r.matched(ctx, mime2)) {
                            render = r;
                            break;
                        }
                    }
                }
            }
        }

        return render;
    }

    /**
     * Json 渲染器
     *
     * @since 3.6
     */
    public Render jsonOf() {
        return get(Constants.AT_JSON);
    }
}