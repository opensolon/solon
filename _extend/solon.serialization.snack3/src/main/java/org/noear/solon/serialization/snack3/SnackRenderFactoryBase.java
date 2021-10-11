package org.noear.solon.serialization.snack3;

import org.noear.snack.core.Constants;
import org.noear.snack.core.NodeEncoder;
import org.noear.solon.serialization.JsonRenderFactory;
import org.noear.solon.serialization.LongConverter;
import org.noear.solon.serialization.StringConverter;

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
    public <T> void addConvertor(Class<T> clz, StringConverter<T> converter) {
        addEncoder(clz, (source, target) -> {
            target.val().setString(converter.convert(source));
        });
    }

    @Override
    public <T> void addConvertor(Class<T> clz, LongConverter<T> converter) {
        addEncoder(clz, (source, target) -> {
            target.val().setNumber(converter.convert(source));
        });
    }
}
