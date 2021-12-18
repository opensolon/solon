package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializer;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class SnackRenderFactory extends SnackRenderFactoryBase {
    public static final SnackRenderFactory global = new SnackRenderFactory();

    private final Options config;
    private SnackRenderFactory(){
        config = Options.def();
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, serializer());
    }

    protected StringSerializer serializer() {
        return new SnackSerializer(config);
    }

    @Override
    protected Options config() {
        return config;
    }
}
