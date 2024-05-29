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
    private final PropertiesStringSerializer serializer = new PropertiesStringSerializer();

    public Options config() {
        return serializer.getConfig();
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false,  serializer);
    }
}