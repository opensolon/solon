package org.noear.solon.serialization;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.serialize.Serializer;

/**
 * 字符串序列化渲染器
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class StringSerializerRender implements Render {
    /**
     * 类型化
     */
    private boolean typed;

    /**
     * 内容类型标签
     */
    private String mimeLabel;

    /**
     * 序列化器
     *
     * @since 2.8
     */
    private Serializer<String> serializer;


    public StringSerializerRender(boolean typed, String mimeLabel, Serializer<String> serializer) {
        this.typed = typed;
        this.mimeLabel = mimeLabel;
        this.serializer = serializer;
    }

    public boolean isTyped() {
        return typed;
    }

    public String getMimeLabel() {
        return mimeLabel;
    }

    public Serializer<String> getSerializer() {
        return serializer;
    }


    @Override
    public String getName() {
        return this.getClass().getSimpleName() + "#" + serializer.name();
    }

    @Override
    public boolean matched(Context ctx, String accept) {
        if (mimeLabel == null || accept == null) {
            return false;
        } else {
            return accept.contains(mimeLabel);
        }
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
        if (SerializationConfig.isOutputMeta()) {
            ctx.headerAdd("solon.serialization", getName());
        }

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