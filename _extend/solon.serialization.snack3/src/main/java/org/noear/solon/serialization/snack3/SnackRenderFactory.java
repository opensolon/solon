package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Constants;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class SnackRenderFactory extends SnackRenderFactoryBase {
    public static final SnackRenderFactory global = new SnackRenderFactory();

    private final Constants config;
    private SnackRenderFactory(){
        config = Constants.def();
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, new SnackSerializer(config));
    }

    @Override
    protected Constants config() {
        return config;
    }
}
