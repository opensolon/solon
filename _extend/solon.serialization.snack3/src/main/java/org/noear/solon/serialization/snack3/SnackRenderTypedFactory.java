package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Constants;
import org.noear.snack.core.NodeEncoder;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.RenderFactory;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 类型化渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class SnackRenderTypedFactory extends SnackCustomizer implements RenderFactory {
    public static final SnackRenderTypedFactory global = new SnackRenderTypedFactory();


    private final Constants config;
    private SnackRenderTypedFactory(){
        config = Constants.serialize();
    }

    @Override
    protected Constants config() {
        return null;
    }

    /**
     * 添加编码器
     * */
    public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
        config.addEncoder(clz, encoder);
    }

    @Override
    public Render create() {
        return new StringSerializerRender(true, new SnackSerializer(config));
    }
}
