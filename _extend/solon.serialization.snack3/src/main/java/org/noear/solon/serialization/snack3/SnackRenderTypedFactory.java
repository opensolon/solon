package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Options;
import org.noear.snack.core.NodeEncoder;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializer;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 类型化渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class SnackRenderTypedFactory extends SnackRenderFactoryBase {
    public static final SnackRenderTypedFactory global = new SnackRenderTypedFactory();


    private final Options config;
    private SnackRenderTypedFactory(){
        config = Options.serialize();
    }


    /**
     * 添加编码器
     * */
    public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
        config.addEncoder(clz, encoder);
    }

    @Override
    public Render create() {
        return new StringSerializerRender(true, serializer());
    }

    @Override
    public StringSerializer serializer() {
        return new SnackSerializer(config);
    }

    @Override
    protected Options config() {
        return config;
    }
}
