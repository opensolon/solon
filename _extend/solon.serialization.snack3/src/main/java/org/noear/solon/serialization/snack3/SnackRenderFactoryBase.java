package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Constants;
import org.noear.snack.core.NodeEncoder;
import org.noear.solon.serialization.JsonRenderFactory;
import org.noear.solon.serialization.JsonLongConverter;
import org.noear.solon.serialization.JsonStringConverter;

/**
 * @author noear 2021/10/11 created
 */
public abstract class SnackRenderFactoryBase implements JsonRenderFactory {

    protected abstract Constants config();

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
        config().addEncoder(clz, encoder);
    }

    @Override
    public <T> void addConvertor(Class<T> clz, JsonStringConverter<T> converter) {
        addEncoder(clz, (source, target) -> {
            target.val().setString(converter.convert(source));
        });
    }

    @Override
    public <T> void addConvertor(Class<T> clz, JsonLongConverter<T> converter) {
        addEncoder(clz, (source, target) -> {
            target.val().setNumber(converter.convert(source));
        });
    }
}
