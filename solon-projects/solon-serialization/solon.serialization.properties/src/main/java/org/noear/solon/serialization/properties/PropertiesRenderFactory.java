package org.noear.solon.serialization.properties;

import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.RenderFactory;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Properties 渲染器工厂
 *
 * @author noear
 * @since 2.7
 */
public class PropertiesRenderFactory  implements RenderFactory {
    private final Options config;

    public Options config() {
        return config;
    }

    public PropertiesRenderFactory() {
        config = Options.def();
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, new PropertiesSerializer(config));
    }
}
