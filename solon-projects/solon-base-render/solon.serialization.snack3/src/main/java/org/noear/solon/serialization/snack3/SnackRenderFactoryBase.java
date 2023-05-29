package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Options;
import org.noear.snack.core.NodeEncoder;
import org.noear.solon.serialization.JsonRenderFactory;
import org.noear.solon.serialization.JsonConverter;

/**
 * Json 渲染器工厂基类
 *
 * @author noear
 * @since 1.5
 */
public abstract class SnackRenderFactoryBase implements JsonRenderFactory {

    public abstract Options config();

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
        config().addEncoder(clz, encoder);
    }

    @Override
    public <T> void addConvertor(Class<T> clz, JsonConverter<T> converter) {
        addEncoder(clz, (source, target) -> {
            Object val = converter.convert((T) source);

            if (val == null) {
                target.asNull();
            } else if (val instanceof String) {
                target.val().setString((String) val);
            } else if (val instanceof Number) {
                target.val().setNumber((Number) val);
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }
        });
    }
}
