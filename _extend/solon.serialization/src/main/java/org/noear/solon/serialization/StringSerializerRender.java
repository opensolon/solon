package org.noear.solon.serialization;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

/**
 * 字符串序列化渲染器
 *
 * @author noear
 * @since 1.5
 */
public class StringSerializerRender implements Render {
    /**
     * 序列化器
     */
    StringSerializer serializer;

    /**
     * 类型化
     */
    boolean typed;

    public StringSerializerRender(boolean typed, StringSerializer serializer) {
        this.typed = typed;
        this.serializer = serializer;
    }

    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        return serializer.serialize(data);
    }

    /**
     * 渲染
     */
    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        String txt = null;

        if (typed) {
            //序列化处理
            //
            txt = serializer.serialize(obj);
        } else {
            //非序列化处理
            //
            if (obj == null) {
                return;
            }

            if (obj instanceof Throwable) {
                throw (Throwable) obj;
            }

            if (obj instanceof String) {
                txt = (String) obj;
            } else {
                txt = serializer.serialize(obj);
            }
        }

        ctx.attrSet("output", txt);

        output(ctx, obj, txt);
    }

    protected void output(Context ctx, Object obj, String txt) {
        if (obj instanceof String && ctx.accept().contains("/json") == false) {
            ctx.output(txt);
        } else {
            ctx.outputAsJson(txt);
        }
    }
}
