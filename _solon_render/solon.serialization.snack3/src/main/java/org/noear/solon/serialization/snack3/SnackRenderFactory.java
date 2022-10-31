package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
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

    private final Options config;

    private SnackRenderFactory() {
        config = Options.def();
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, new SnackSerializer(config));
    }

    @Override
    public Options config() {
        return config;
    }

    /**
     * 重新设置特性
     */
    public void setFeatures(Feature... features) {
        config.setFeatures(features);
    }

    /**
     * 添加特性
     * */
    public void addFeatures(Feature... features) {
        config.add(features);
    }

    /**
     * 移除特性
     * */
    public void removeFeatures(Feature... features) {
        config.remove(features);
    }
}
